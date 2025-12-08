<?php

declare(strict_types=1);

namespace App\User\Service;

use App\User\Entity\User;
use Doctrine\ORM\EntityManagerInterface;

final class UserCreator
{
    public function __construct(
        private EntityManagerInterface $em,
        private UserAutologinTokenCreator $autologinTokenCreator,
    ) {
    }

    public function create(string $email, string $timezone, bool $isEmailReal, ?string $fullName): User
    {
        $fullName ??= '';

        $user = new User();
        $user->setEmail($email);
        $user->setIsEmailReal($isEmailReal);
        $user->setTimezone($timezone);
        $user->setFullName($this->ucwords($fullName));

        $this->em->persist($user);
        $this->em->flush();

        $this->autologinTokenCreator->createForUser($user);

        return $user;
    }

    private function ucwords(string $str): string
    {
        $str = mb_strtolower($str);
        $parts = explode(" ", $str);
        foreach($parts as $key => $part) {
            $part = mb_strtoupper(mb_substr($part, 0, 1)) . mb_substr($part, 1);
            $parts[$key] = $part;
        }

        return implode(" ", $parts);
    }
}
