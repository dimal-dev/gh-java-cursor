<?php

namespace App\Psiholog\Controller;

use App\Billing\Entity\UserWallet;
use App\Billing\Entity\UserWalletOperation;
use App\Billing\Repository\UserWalletManager;
use App\Billing\Repository\UserWalletRepository;
use App\Common\Service\ConsultationAllowedCancelTimeChecker;
use App\Notification\Service\EmailSender;
use App\Psiholog\Entity\PsihologProfile;
use App\Psiholog\Entity\PsihologSchedule;
use App\Psiholog\Repository\PsihologProfileRepository;
use App\Psiholog\Service\CurrentPsihologRetriever;
use App\Psiholog\Service\TelegramNotifier;
use App\User\Entity\UserConsultation;
use App\User\Entity\UserConsultationPsihologSchedule;
use App\User\Repository\UserConsultationPsihologScheduleRepository;
use App\User\Repository\UserConsultationRepository;
use App\User\Service\RouteNames;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;

#[Route(
    "/cancel-consultation",
    name: "cancel_consultation",
    methods: ["POST"]
)]
final class CancelConsultationController extends AbstractController
{

    public function __construct(
        private UrlGeneratorInterface $urlGenerator,
        private UserConsultationRepository $userConsultationRepository,
        private UserConsultationPsihologScheduleRepository $userConsultationPsihologScheduleRepository,
        private UserWalletRepository $userWalletRepository,
        private UserWalletManager $userWalletManager,
        private EntityManagerInterface $em,
        private ConsultationAllowedCancelTimeChecker $consultationAllowedCancelTimeChecker,
        private TelegramNotifier $telegramNotifier,
        private CurrentPsihologRetriever $currentPsihologRetriever,
        private PsihologProfileRepository $psihologProfileRepository,
        private EmailSender $emailSender,
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $this->cancel($request);

        return $this->redirectBack($request);

    }

    private function redirectBack(Request $request): RedirectResponse
    {
        $url = $request->server->get('HTTP_REFERER', $this->urlGenerator->generate(RouteNames::DASHBOARD));

        return $this->redirect($url);
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

    private function cancel(Request $request): void
    {
        $userConsultationId = (int) $request->request->get('user_consultation_id');
        if (empty($userConsultationId)) {
            return;
        }

        $userConsultation = $this->userConsultationRepository->findById($userConsultationId);
        if (!$userConsultation) {
            return;
        }

        $psiholog = $this->currentPsihologRetriever->get();
        if ($userConsultation->getPsihologId() !== $psiholog->getId()) {
            return;
        }

        $userConsultationPsihologScheduleList = $this->userConsultationPsihologScheduleRepository->findById(
            $userConsultation->getId()
        );

        if (empty($userConsultationPsihologScheduleList)) {
            return;
        }

        /** @var PsihologSchedule $firstPsihologSchedule */
        $firstPsihologSchedule = $userConsultationPsihologScheduleList[0]->getPsihologSchedule();
        /** @var UserConsultationPsihologSchedule $item */
        foreach ($userConsultationPsihologScheduleList as $item) {
            $psihologSchedule = $item->getPsihologSchedule();
            if ($firstPsihologSchedule->getAvailableAt() > $psihologSchedule->getAvailableAt()) {
                $firstPsihologSchedule = $psihologSchedule;
            }
            $psihologSchedule->setState($psihologSchedule::STATE_UNAVAILABLE);
        }

        $psihologProfile = $this->psihologProfileRepository->findByPsiholog($userConsultation->getPsiholog());
        if ($this->consultationAllowedCancelTimeChecker->isAllowed($firstPsihologSchedule->getAvailableAt())) {
            $this->handleCancelledInTime($userConsultation, $psihologProfile, $firstPsihologSchedule);
        } else {
            $this->handleCancelledNotInTime($userConsultation, $psihologProfile, $firstPsihologSchedule);
        }
        $this->em->flush();
    }

    private function handleCancelledInTime(
        UserConsultation $userConsultation,
        ?PsihologProfile $psihologProfile,
        PsihologSchedule $firstPsihologSchedule
    ): void {
        $userConsultation->setState($userConsultation::STATE_CANCELLED_BY_PSIHOLOG_IN_TIME);

        $userWallet = $this->userWalletRepository->findByUser($userConsultation->getUser());
        $this->addMoneyToWallet(
            $userConsultation,
            $userWallet,
            UserWalletOperation::REASON_CANCELLED_CONSULTATION
        );

        try {
            $this->emailSender->sendConsultationCancelled(
                $userConsultation->getUser(),
                $psihologProfile,
                $firstPsihologSchedule->getAvailableAt()
            );
        } catch (\Exception $e) {
        }

        try {
            $this->telegramNotifier->notifyConsultationCancelledByPsiholog($firstPsihologSchedule, true);
        } catch (\Exception $e) {
        }
    }

    private function handleCancelledNotInTime(
        UserConsultation $userConsultation,
        ?PsihologProfile $psihologProfile,
        PsihologSchedule $firstPsihologSchedule
    ): void {
        $userConsultation->setState($userConsultation::STATE_CANCELLED_BY_PSIHOLOG_NOT_IN_TIME);

        $userWallet = $this->userWalletRepository->findByUser($userConsultation->getUser());
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

        try {
            $this->emailSender->sendConsultationCancelled(
                $userConsultation->getUser(),
                $psihologProfile,
                $firstPsihologSchedule->getAvailableAt(),
                true,
                $userConsultation->getPsihologPrice(),
            );
        } catch (\Exception $e) {
        }

        try {
            $this->telegramNotifier->notifyConsultationCancelledByPsiholog($firstPsihologSchedule, false);
        } catch (\Exception $e) {
        }
    }
}