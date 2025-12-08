<?php

declare(strict_types=1);

namespace App\Psiholog\Entity;

use Doctrine\ORM\Mapping as ORM;

#[ORM\Table(name: 'psiholog_profile')]
#[ORM\Entity]
class PsihologProfile
{
    public const SEX_WOMAN = 1;
    public const SEX_MAN = 2;

    #[ORM\Column(name: 'id', type: 'integer', nullable: false, options: ['unsigned' => true])]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'AUTO')]
    private int $id;

    #[ORM\Column(name: 'first_name', type: 'string')]
    private string $firstName;

    #[ORM\Column(name: 'last_name', type: 'string')]
    private string $lastName;

    #[ORM\Column(name: 'birth_date', type: 'string')]
    private string $birthDate;

    #[ORM\Column(name: 'works_from', type: 'string')]
    private string $worksFrom;

    #[ORM\Column(name: 'profile_template', type: 'string')]
    private string $profileTemplate;

    #[ORM\Column(name: 'sex', type: 'integer')]
    private int $sex;

    #[ORM\Column(name: 'psiholog_id', type: 'integer')]
    private int $psihologId;

    #[ORM\OneToOne(targetEntity: 'Psiholog', cascade: ['persist'])]
    private Psiholog $psiholog;

    public function getId(): int
    {
        return $this->id;
    }

    public function getFirstName(): string
    {
        return $this->firstName;
    }

    public function setFirstName(string $firstName): void
    {
        $this->firstName = $firstName;
    }

    public function getLastName(): string
    {
        return $this->lastName;
    }

    public function setLastName(string $lastName): void
    {
        $this->lastName = $lastName;
    }

    public function getBirthDate(): string
    {
        return $this->birthDate;
    }

    public function setBirthDate(string $birthDate): void
    {
        $this->birthDate = $birthDate;
    }

    public function getWorksFrom(): string
    {
        return $this->worksFrom;
    }

    public function setWorksFrom(string $worksFrom): void
    {
        $this->worksFrom = $worksFrom;
    }

    public function getProfileTemplate(): string
    {
        return $this->profileTemplate;
    }

    public function setProfileTemplate(string $profileTemplate): void
    {
        $this->profileTemplate = $profileTemplate;
    }

    public function getPsiholog(): Psiholog
    {
        return $this->psiholog;
    }

    public function setPsiholog(Psiholog $psiholog): void
    {
        $this->psiholog = $psiholog;
    }

    public function isWoman(): bool
    {
        return $this->sex === self::SEX_WOMAN;
    }

    public function isMan(): bool
    {
        return $this->sex === self::SEX_MAN;
    }

    public function getFullName(): string
    {
        return "{$this->getFirstName()} {$this->getLastName()}";
    }

    public function getPsihologId(): int
    {
        return $this->psihologId;
    }
}
