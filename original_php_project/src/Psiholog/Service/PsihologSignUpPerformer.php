<?php

declare(strict_types=1);

namespace App\Psiholog\Service;

use App\Notification\Service\EmailSender;
use App\Psiholog\Entity\Psiholog;
use App\Psiholog\Service\PsihologAutologinTokenCreator;
use Doctrine\ORM\EntityManagerInterface;

final class PsihologSignUpPerformer
{
    public function __construct(
        private EntityManagerInterface         $em,
        private PsihologAutologinTokenCreator $autologinTokenCreator,
        private EmailSender                    $emailSender
    ) {
    }

    public function signUp(string $email, int $role, bool $sendEmail = false): Psiholog
    {
        $psiholog = new Psiholog();
        $psiholog->setEmail($email);
        $psiholog->setRole($role);

        $this->em->persist($psiholog);
        $this->em->flush();

        $this->autologinTokenCreator->createForPsiholog($psiholog);

        if ($sendEmail) {
            $this->sendAfterSignUpEmail($psiholog);
        }

        return $psiholog;
    }

    public function sendAfterSignUpEmail(Psiholog $user)
    {
        $this->emailSender->sendAutologinEmail($user->getEmail(), $user->getPsihologAutologinToken()->getToken());
    }
}
