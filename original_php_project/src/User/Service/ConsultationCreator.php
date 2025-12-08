<?php

namespace App\User\Service;

use App\Billing\Entity\UserWallet;
use App\Billing\Entity\UserWalletOperation;
use App\Billing\Repository\UserWalletManager;
use App\Psiholog\Entity\PsihologPrice;
use App\Psiholog\Entity\PsihologSchedule;
use App\Psiholog\Repository\PsihologScheduleRepository;
use App\Psiholog\Service\TelegramNotifier;
use App\User\Entity\User;
use App\User\Entity\UserConsultation;
use App\User\Entity\UserConsultationPsihologSchedule;
use App\User\Entity\UserPsiholog;
use App\User\Repository\UserPsihologRepository;
use Doctrine\ORM\EntityManagerInterface;

class ConsultationCreator
{
    public function __construct(
        private EntityManagerInterface $em,
        private UserWalletManager $userWalletManager,
        private UserPsihologRepository $userPsihologRepository,
        private TelegramNotifier $telegramNotifier,
        private PsihologScheduleRepository $psihologScheduleRepository,
    ) {
    }

    public function setupConsultation(
        PsihologPrice $price,
        PsihologSchedule $psihologSchedule,
        UserWallet $userWallet
    ): ?UserConsultation {
        $user = $userWallet->getUser();

        $this->addPsihologToUser($user, $price);
        $this->em->flush();

        $psihologScheduleList = $this->findPsihologScheduleList($psihologSchedule, $price);
        if ($psihologScheduleList === null) {
            return null;
        }

        $this->em->getConnection()->beginTransaction();
        try {
            $userConsultation = $this->createUserConsultation($user, $price);
            $this->em->flush();

            $this->addPsihologScheduleToUserConsultation($psihologScheduleList, $userConsultation);
            $this->removeMoneyFromWallet($userConsultation, $userWallet);
            $this->em->flush();

            $this->em->getConnection()->commit();

            try {
                $this->telegramNotifier->notifyConsultationBooked($user, $psihologSchedule);
            } catch (\Exception $e) {
            }
            try {
                $this->telegramNotifier->notifyAdminsConsultationBooked($user, $psihologSchedule);
            } catch (\Exception $e) {
            }
            //todo:
            //уведомить пользователя об успешной оплате и тому что запрос отправлен

            return $userConsultation;
        } catch (\Throwable $e) {
            $this->em->getConnection()->rollBack();

            //todo log into sentry here
            throw $e;
        }
    }

    private function addPsihologToUser(User $user, PsihologPrice $price): void
    {
        $psiholog = $price->getPsiholog();
        if (!$this->userPsihologRepository->isUserBelongsToPsiholog($user->getId(), $psiholog->getId())) {
            $userPsiholog = new UserPsiholog();
            $userPsiholog->setPsiholog($psiholog);
            $userPsiholog->setUser($user);
            $this->em->persist($userPsiholog);
            $this->em->flush();
        }
    }

    private function createUserConsultation(User $user, PsihologPrice $price): UserConsultation
    {
        $userConsultation = new UserConsultation();
        $userConsultation->setUser($user);
        $userConsultation->setPsiholog($price->getPsiholog());
        $userConsultation->setPsihologPrice($price);
        $userConsultation->setState($userConsultation::STATE_CREATED);
        if ($price->isIndividual()) {
            $userConsultation->setType($userConsultation::TYPE_INDIVIDUAL);
        } else if ($price->isCouple()) {
            $userConsultation->setType($userConsultation::TYPE_COUPLE);
        } else {
            throw new \RuntimeException("Unknown price type {$price->getType()}");
        }

        $this->em->persist($userConsultation);

        return $userConsultation;
    }

    /**
     * @param PsihologSchedule[] $psihologScheduleList
     */
    private function addPsihologScheduleToUserConsultation(
        array $psihologScheduleList,
        UserConsultation $userConsultation
    ): void {
        foreach ($psihologScheduleList as $psihologSchedule) {
            $psihologSchedule->setState($psihologSchedule::STATE_BOOKED);
            $userConsultationPsihologSchedule = new UserConsultationPsihologSchedule();
            $userConsultationPsihologSchedule->setPsihologSchedule($psihologSchedule);
            $userConsultationPsihologSchedule->setUserConsultation($userConsultation);
            $this->em->persist($userConsultationPsihologSchedule);
        }
    }

    private function removeMoneyFromWallet(
        UserConsultation $userConsultation,
        UserWallet $userWallet
    ): void {
        $price = $userConsultation->getPsihologPrice();

        $userWalletOperation = new UserWalletOperation();
        $userWalletOperation->setCurrency($price->getCurrency());
        $userWalletOperation->setAmount($price->getPriceInCents());
        $userWalletOperation->setType(UserWalletOperation::TYPE_SUBTRACT);
        $userWalletOperation->setReasonType(UserWalletOperation::REASON_CREATED_CONSULTATION);
        $userWalletOperation->setReasonId($userConsultation->getId());
        $userWalletOperation->setUserWallet($userWallet);
        $this->em->persist($userWalletOperation);

        $this->userWalletManager->applyOperation($userWallet, $userWalletOperation);
    }

    private function findPsihologScheduleList(PsihologSchedule $psihologSchedule, PsihologPrice $price): ?array
    {
        if (!$psihologSchedule->isAvailable()) {
            return null;
        }

        $psiholog = $psihologSchedule->getPsiholog();
        $from = clone $psihologSchedule->getAvailableAt();

        if ($price->isIndividual()) {
            $to = (clone $from)->modify("+30 minute");
        } else if ($price->isCouple()) {
            $to = (clone $from)->modify("+60 minute");
        }

        $availableScheduleList = $this->psihologScheduleRepository->findBetween($psiholog, $from, $to);

        if ($price->isIndividual() && count($availableScheduleList) === 2) {
            return $availableScheduleList;
        }
        if ($price->isCouple() && count($availableScheduleList) === 3) {
            return $availableScheduleList;
        }

        return null;
    }
}
