<?php

declare(strict_types=1);

namespace App\Staff\Service;

use App\Notification\Service\EmailSender;
use App\Staff\Entity\User;
use App\Staff\Service\UserAutologinTokenCreator;
use Doctrine\ORM\EntityManagerInterface;

final class UserSignUpPerformer
{
    private EntityManagerInterface $em;
    private UserAutologinTokenCreator $autologinTokenCreator;
    private EmailSender $emailSender;

    public function __construct(
        EntityManagerInterface $em,
        UserAutologinTokenCreator $autologinTokenCreator,
        EmailSender $emailSender
    ) {
        $this->em = $em;
        $this->autologinTokenCreator = $autologinTokenCreator;
        $this->emailSender = $emailSender;
    }

    public function signUp(string $email, int $role, bool $sendEmail = false): User
    {
        $user = new User();
        $user->setEmail($email);
        $user->setRole($role);

        $this->em->persist($user);
        $this->em->flush();

        $this->autologinTokenCreator->createForUser($user);

        if ($sendEmail) {
            $this->sendAfterSignUpEmail($user);
        }

        return $user;
    }

    public function sendAfterSignUpEmail(User $user)
    {
        $this->emailSender->sendAutologinEmail($user->getEmail(), $user->getUserAutologinToken()->getToken());
    }
}
