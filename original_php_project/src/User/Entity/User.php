<?php

declare(strict_types=1);

namespace App\User\Entity;

use Doctrine\ORM\Mapping as ORM;

#[ORM\Table(name: 'user')]
#[ORM\Entity]
class User
{
    public const DEFAULT_LOCALE = 'ua';
    public const FAKE_USER_EMAIL_PART = 'gh.fake.ue.';

    #[ORM\Column(name: 'id', type: 'integer', nullable: false, options: ['unsigned' => true])]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'AUTO')]
    private int $id;

    #[ORM\Column(name: 'email', type: 'string', length: 500, nullable: false)]
    private string $email;

    #[ORM\Column(name: 'full_name', type: 'string', length: 500, nullable: false)]
    private ?string $fullName = '';

    #[ORM\Column(name: 'is_full_name_set_by_user', type: 'boolean')]
    private bool $isFullNameSetByUser = false;

    #[ORM\Column(name: 'timezone', type: 'string')]
    private string $timezone;

    #[ORM\Column(name: 'locale', type: 'string')]
    private string $locale = self::DEFAULT_LOCALE;

    #[ORM\Column(name: 'is_email_real', type: 'boolean')]
    private bool $isEmailReal = true;

    #[ORM\OneToOne(targetEntity: 'UserAutologinToken', mappedBy: 'user', cascade: ['persist'])]
    private UserAutologinToken $userAutologinToken;

    #[ORM\OneToOne(targetEntity: 'App\User\Entity\UserPsiholog', mappedBy: 'user', cascade: ['persist'])]
    private ?UserPsiholog $userPsiholog;

    public function getId(): int
    {
        return $this->id;
    }
    public function getEmail(): string
    {
        return $this->email;
    }
    public function setEmail(string $email): void
    {
        $this->email = $email;
    }
    public function setUserAutologinToken(UserAutologinToken $userAutologinToken): void
    {
        $this->userAutologinToken = $userAutologinToken;
    }
    public function getUserAutologinToken(): UserAutologinToken
    {
        return $this->userAutologinToken;
    }
    public function getUserPsiholog(): ?UserPsiholog
    {
        return $this->userPsiholog;
    }
    public function setUserPsiholog(?UserPsiholog $userPsiholog): void
    {
        $this->userPsiholog = $userPsiholog;
    }
    public function isEmailReal(): bool
    {
        return $this->isEmailReal;
    }

    public function setIsEmailReal(bool $isEmailReal): void
    {
        $this->isEmailReal = $isEmailReal;
    }

    public function getTimezone(): string
    {
        return $this->timezone;
    }

    public function setTimezone(string $timezone): void
    {
        $this->timezone = $timezone;
    }

    public function getFullName(): string
    {
        return $this->fullName ?? '';
    }

    public function setFullName(string $fullName): void
    {
        $this->fullName = $fullName;
    }

    public function isFullNameSetByUser(): bool
    {
        return $this->isFullNameSetByUser;
    }

    public function setIsFullNameSetByUser(bool $isFullNameSetByUser): void
    {
        $this->isFullNameSetByUser = $isFullNameSetByUser;
    }

    public function getLocale(): string
    {
        return $this->locale;
    }

    public function setLocale(string $locale): void
    {
        $this->locale = $locale;
    }
}
