<?php

namespace App\Psiholog\Controller;

use App\Billing\Entity\UserWallet;
use App\Billing\Entity\UserWalletOperation;
use App\Billing\Repository\UserWalletManager;
use App\Billing\Repository\UserWalletRepository;
use App\Common\Service\ConsultationAllowedCancelTimeChecker;
use App\Notification\Service\EmailSender;
use App\Psiholog\Entity\Psiholog;
use App\Psiholog\Entity\PsihologSchedule;
use App\Psiholog\Repository\PsihologProfileRepository;
use App\Psiholog\Repository\PsihologScheduleRepository;
use App\Psiholog\Service\CurrentPsihologRetriever;
use App\Psiholog\Service\TelegramNotifier;
use App\Psiholog\Service\TimeHelper;
use App\User\Entity\UserConsultation;
use App\User\Repository\UserConsultationPsihologScheduleRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/schedule-settings/book-time-ajax",
    name: "schedule_settings_book_time_ajax",
    methods: ["POST"]
)]
final class ScheduleSettingsBookTimeAjaxController extends AbstractController
{
    public const ACTION_ADD = 1;
    public const ACTION_REMOVE = 2;

    public function __construct(
        private EntityManagerInterface $em,
        private CurrentPsihologRetriever $currentPsihologRetriever,
        private PsihologScheduleRepository $psihologScheduleRepository,
        private TimeHelper $timeHelper,
        private UserConsultationPsihologScheduleRepository $userConsultationPsihologScheduleRepository,
        private ConsultationAllowedCancelTimeChecker $consultationAllowedCancelTimeChecker,
        private UserWalletRepository $userWalletRepository,
        private UserWalletManager $userWalletManager,
        private EmailSender $emailSender,
        private PsihologProfileRepository $psihologProfileRepository,
        private TelegramNotifier $telegramNotifier,
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $psiholog = $this->currentPsihologRetriever->get();
        $redirectResponse = $request->request->has('redirectTo') ? $this->redirect($request->request->get('redirectTo')) : null;

        if (!$psiholog) {
            return $redirectResponse ?? new JsonResponse([1]);
        }
        $availableAt = $request->request->get('time');
        if (strtotime($availableAt) < 1) {
            return $redirectResponse ?? new JsonResponse([2]);
        }
        $action = (int) $request->request->get('action');
        if ($action !== self::ACTION_ADD && $action !== self::ACTION_REMOVE) {
            return $redirectResponse ?? new JsonResponse([2]);
        }

        $availableAtUtc = $this->timeHelper->toUtcTzDateTime($availableAt);
        
        $schedule = $this->psihologScheduleRepository->findByTime($psiholog, $availableAtUtc);
        
        if ($action === self::ACTION_ADD) {
            $this->handleAdd($schedule, $psiholog, $availableAtUtc);
        }

        if ($action === self::ACTION_REMOVE) {
            $this->handleRemove($schedule);
        }

        return $redirectResponse ?? new JsonResponse(['success' => true]);
    }

    private function handleRemove(PsihologSchedule $schedule): void
    {
        if ($schedule->isBooked()) {
            $userConsultationPsihologSchedule = $this->userConsultationPsihologScheduleRepository->findBySchedule(
                $schedule
            );
            if (!$userConsultationPsihologSchedule) {
                return;
            }

            $userConsultation = $userConsultationPsihologSchedule->getUserConsultation();

            $schedule->setState($schedule::STATE_UNAVAILABLE);
            $psihologProfile = $this->psihologProfileRepository->findByPsiholog($schedule->getPsiholog());
            if ($this->consultationAllowedCancelTimeChecker->isAllowed($schedule->getAvailableAt())) {
                $userConsultation->setState($userConsultation::STATE_CANCELLED_BY_PSIHOLOG_IN_TIME);

                $userWallet = $this->userWalletRepository->findByUser($userConsultation->getUser());
                if ($userWallet) {
                    $this->addMoneyToWallet(
                        $userConsultation,
                        $userWallet,
                        UserWalletOperation::REASON_CANCELLED_CONSULTATION
                    );
                }

                try {
                    $this->emailSender->sendConsultationCancelled(
                        $userConsultation->getUser(),
                        $psihologProfile,
                        $schedule->getAvailableAt()
                    );
                } catch (\Exception $e) {
                }

                try {
                    $this->telegramNotifier->notifyConsultationCancelledByPsiholog(
                        $userConsultationPsihologSchedule->getPsihologSchedule(),
                        true
                    );
                } catch (\Exception $e) {
                }
            } else {
                $userConsultation->setState($userConsultation::STATE_CANCELLED_BY_PSIHOLOG_NOT_IN_TIME);

                $userWallet = $this->userWalletRepository->findByUser($userConsultation->getUser());
                if ($userWallet) {
                    $this->addMoneyToWallet(
                        $userConsultation,
                        $userWallet,
                        UserWalletOperation::REASON_CANCELLED_CONSULTATION
                    );

                    $this->addMoneyToWallet(
                        $userConsultation,
                        $userWallet,
                        UserWalletOperation::REASON_CANCELLED_CONSULTATION_NOT_IN_TIME_BY_PSIHOLOG
                    );
                }

                try {
                    $this->emailSender->sendConsultationCancelled(
                        $userConsultation->getUser(),
                        $psihologProfile,
                        $schedule->getAvailableAt(),
                        true,
                        $userConsultation->getPsihologPrice(),
                    );
                } catch (\Exception $e) {
                }

                try {
                    $this->telegramNotifier->notifyConsultationCancelledByPsiholog(
                        $userConsultationPsihologSchedule->getPsihologSchedule(),
                        false
                    );
                } catch (\Exception $e) {
                }
            }
        }
        $schedule->setState($schedule::STATE_UNAVAILABLE);
        $this->em->flush();
    }

    private function addMoneyToWallet(
        UserConsultation $userConsultation,
        UserWallet $userWallet,
        int $reason
    ): void {
        $price = $userConsultation->getPsihologPrice();

        $userWalletOperation = new UserWalletOperation();
        $userWalletOperation->setCurrency($price->getCurrency());
        $userWalletOperation->setAmount($price->getPriceInCents());
        $userWalletOperation->setType(UserWalletOperation::TYPE_ADD);
        $userWalletOperation->setReasonType($reason);
        $userWalletOperation->setReasonId($userConsultation->getId());
        $userWalletOperation->setUserWallet($userWallet);
        $this->em->persist($userWalletOperation);

        $this->userWalletManager->applyOperation($userWallet, $userWalletOperation);
    }

    private function handleAdd(
        ?PsihologSchedule $schedule,
        Psiholog $psiholog,
        \DateTime $availableAtUtc
    ): void {
        if ($schedule?->isUnAvailable()) {
            $schedule->setState($schedule::STATE_AVAILABLE);
        }

        if (!$schedule) {
            $schedule = new PsihologSchedule();
            $schedule->setPsiholog($psiholog);
            $schedule->setAvailableAt($availableAtUtc);

            $this->em->persist($schedule);
        }

        $this->em->flush();
    }
}