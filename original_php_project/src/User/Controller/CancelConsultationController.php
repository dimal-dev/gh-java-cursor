<?php

namespace App\User\Controller;

use App\Billing\Entity\UserWallet;
use App\Billing\Entity\UserWalletOperation;
use App\Billing\Repository\UserWalletManager;
use App\Billing\Repository\UserWalletRepository;
use App\Common\Service\ConsultationAllowedCancelTimeChecker;
use App\Psiholog\Entity\PsihologPrice;
use App\Psiholog\Entity\PsihologSchedule;
use App\Psiholog\Service\TelegramNotifier;
use App\User\Entity\User;
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
use Symfony\Component\Security\Core\Security;

#[Route(
    "/cancel-consultation",
    name: "cancel_consultation",
    methods: ["GET"]
)]
final class CancelConsultationController extends AbstractController
{

    public function __construct(
        private Security $security,
        private UrlGeneratorInterface $urlGenerator,
        private UserConsultationRepository $userConsultationRepository,
        private UserConsultationPsihologScheduleRepository $userConsultationPsihologScheduleRepository,
        private UserWalletRepository $userWalletRepository,
        private UserWalletManager $userWalletManager,
        private EntityManagerInterface $em,
        private ConsultationAllowedCancelTimeChecker $consultationAllowedCancelTimeChecker,
        private TelegramNotifier $telegramNotifier,
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $this->cancel($request);

        return $this->redirectBack($request);

    }

    private function redirectBack(Request $request): RedirectResponse
    {
        $url = $this->urlGenerator->generate(RouteNames::DASHBOARD);

        return $this->redirect($url);
    }

    private function addMoneyToWallet(
        UserConsultation $userConsultation,
        UserWallet $userWallet
    ): void {
        $price = $userConsultation->getPsihologPrice();

        $userWalletOperation = new UserWalletOperation();
        $userWalletOperation->setCurrency($price->getCurrency());
        $userWalletOperation->setAmount($price->getPriceInCents());
        $userWalletOperation->setType(UserWalletOperation::TYPE_ADD);
        $userWalletOperation->setReasonType(UserWalletOperation::REASON_CANCELLED_CONSULTATION);
        $userWalletOperation->setReasonId($userConsultation->getId());
        $userWalletOperation->setUserWallet($userWallet);
        $this->em->persist($userWalletOperation);

        $this->userWalletManager->applyOperation($userWallet, $userWalletOperation);
    }

    private function cancel(Request $request): void
    {
        $userConsultationId = (int) $request->query->get('id');
        if (empty($userConsultationId)) {
            return;
        }

        /** @var User $user */
        $user = $this->security->getUser()->getUser();
        $userConsultation = $this->userConsultationRepository->findById($userConsultationId);

        if (!$userConsultation) {
            return;
        }

        if ($user->getId() !== $userConsultation->getUserId()) {
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
            $psihologSchedule->setState($psihologSchedule::STATE_AVAILABLE);
        }

        if ($this->consultationAllowedCancelTimeChecker->isAllowed($firstPsihologSchedule->getAvailableAt())) {
            $userConsultation->setState($userConsultation::STATE_CANCELLED_BY_USER_IN_TIME);
            $userWallet = $this->userWalletRepository->findByUser($user);
            $this->addMoneyToWallet($userConsultation, $userWallet);

            try {
                $this->telegramNotifier->notifyConsultationCancelledByUser(
                    $firstPsihologSchedule,
                    true
                );
            } catch (\Exception $e) {
            }
        } else {
            $userConsultation->setState($userConsultation::STATE_CANCELLED_BY_USER_NOT_IN_TIME);

            try {
                $this->telegramNotifier->notifyConsultationCancelledByUser(
                    $firstPsihologSchedule,
                    false
                );
            } catch (\Exception $e) {
            }
        }
        $this->em->flush();

        try {
            $this->telegramNotifier->notifyConsultationCancelled($firstPsihologSchedule);
        } catch (\Exception $e) {
        }
    }
}