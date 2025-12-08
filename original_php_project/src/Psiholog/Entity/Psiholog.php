<?php

declare(strict_types=1);

namespace App\Psiholog\Entity;

use Doctrine\ORM\Mapping as ORM;

#[ORM\Table(name: 'psiholog')]
#[ORM\Entity]
class Psiholog
{
    public const ROLE_PSIHOLOG = 1;
    public const ROLE_TEST_PSIHOLOG = 2;
    public const ROLE_LIST = [
        self::ROLE_PSIHOLOG,
        self::ROLE_TEST_PSIHOLOG,
    ];
    public const STATE_ACTIVE = 1;

    #[ORM\Column(name: 'id', type: 'integer', nullable: false, options: ['unsigned' => true])]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'AUTO')]
    private int $id;

    #[ORM\Column(name: 'email', type: 'string', length: 255)]
    private string $email;

    #[ORM\Column(name: 'role', type: 'integer')]
    private int $role;

    #[ORM\OneToOne(targetEntity: 'PsihologAutologinToken', mappedBy: 'psiholog', cascade: ['persist'])]
    private PsihologAutologinToken $psihologAutologinToken;

    #[ORM\Column(name: 'state', type: 'integer')]
    private int $state = self::STATE_ACTIVE;

    public function getId(): int
    {
        return $this->id;
    }
    public function getEmail(): string
    {
        return $this->email;
    }
    public function getRole(): int
    {
        return $this->role;
    }
    public function getState(): int
    {
        return $this->state;
    }
    public function setState(int $state): void
    {
        $this->state = $state;
    }
    public function setEmail(string $email): void
    {
        $this->email = $email;
    }
    public function setRole(int $role): void
    {
        $this->role = $role;
    }
    public function getPsihologAutologinToken(): PsihologAutologinToken
    {
        return $this->psihologAutologinToken;
    }
    public function setPsihologAutologinToken(PsihologAutologinToken $psihologAutologinToken): void
    {
        $this->psihologAutologinToken = $psihologAutologinToken;
    }
    public function isActive(): bool
    {
        return $this->state === self::STATE_ACTIVE;
    }

    public const PSIHOLOG_ID_ZERO_MAN = 66666666;

    public const PSIHOLOG_ID_LIST = [
        self::PSIHOLOG_ID_ALISA_CHIZIKOVA,
        self::PSIHOLOG_ID_INNA_KARASHCHUK,
        self::PSIHOLOG_ID_VIKTORIYA_RADIY,
        self::PSIHOLOG_ID_OKSANA_RYZHIKOVA,
        self::PSIHOLOG_ID_VALENTINA_DOVBYSH,
        self::PSIHOLOG_ID_YULIYA_GURNEVICH,
        self::PSIHOLOG_ID_VIKTOR_DICHKO,
        self::PSIHOLOG_ID_MARIYA_KOROYED,
        self::PSIHOLOG_ID_SAN_SATTARI,
        self::PSIHOLOG_ID_ARTEM_KHMELEVSKIY,
        self::PSIHOLOG_ID_NADEZHDA_TORDIYA,
        self::PSIHOLOG_ID_NATALYA_PONOMAREVA,
        self::PSIHOLOG_ID_YULIYA_KANTAROVICH,
        self::PSIHOLOG_ID_IRYNA_KOLODA,
        self::PSIHOLOG_ID_MIRABELLA_BELILOVSKA,
    ];

    public const PSIHOLOG_ID_ALISA_CHIZIKOVA = 1;
    public const PSIHOLOG_ID_INNA_KARASHCHUK = 2;
    public const PSIHOLOG_ID_VIKTORIYA_RADIY = 3;
    public const PSIHOLOG_ID_OKSANA_RYZHIKOVA = 4;
    public const PSIHOLOG_ID_VALENTINA_DOVBYSH = 5;
    public const PSIHOLOG_ID_YULIYA_GURNEVICH = 6;
    public const PSIHOLOG_ID_VIKTOR_DICHKO = 7;
    public const PSIHOLOG_ID_MARIYA_KOROYED = 8;
    public const PSIHOLOG_ID_SAN_SATTARI = 9;
    public const PSIHOLOG_ID_ARTEM_KHMELEVSKIY = 10;
    public const PSIHOLOG_ID_NADEZHDA_TORDIYA = 11;
    public const PSIHOLOG_ID_NATALYA_PONOMAREVA = 12;
    public const PSIHOLOG_ID_YULIYA_KANTAROVICH = 13;
    public const PSIHOLOG_ID_IRYNA_KOLODA = 14;
    public const PSIHOLOG_ID_MIRABELLA_BELILOVSKA = 15;
}
