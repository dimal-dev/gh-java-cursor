<?php

declare(strict_types=1);

namespace App\User\Entity;

use App\Psiholog\Entity\Psiholog;
use App\Psiholog\Entity\PsihologPrice;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Table(name: 'user_consultation')]
#[ORM\Entity]
class UserConsultation
{
    public const STATE_CREATED = 1;
    public const STATE_COMPLETED = 2;

    public const TYPE_INDIVIDUAL = 1;
    public const TYPE_COUPLE = 2;

    public const STATE_CANCELLED_BY_USER_IN_TIME = 5;
    public const STATE_CANCELLED_BY_USER_NOT_IN_TIME = 6;
    public const STATE_CANCELLED_BY_PSIHOLOG_IN_TIME = 7;
    public const STATE_CANCELLED_BY_PSIHOLOG_NOT_IN_TIME = 8;

    #[ORM\Column(name: 'id', type: 'integer', nullable: false, options: ['unsigned' => true])]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'AUTO')]
    private int $id;

    #[ORM\Column(name: 'user_id', type: 'integer')]
    private int $userId;

    #[ORM\Column(name: 'psiholog_id', type: 'integer')]
    private int $psihologId;

    #[ORM\Column(name: 'psiholog_price_id', type: 'integer')]
    private int $psihologPriceId;

    #[ORM\Column(name: 'state', type: 'integer')]
    private int $state;

    #[ORM\Column(name: 'type', type: 'integer')]
    private int $type = self::TYPE_INDIVIDUAL;

    #[ORM\OneToOne(targetEntity: '\App\User\Entity\User')]
    #[ORM\JoinColumn(name: 'user_id', referencedColumnName: 'id')]
    private User $user;

    #[ORM\OneToOne(targetEntity: '\App\Psiholog\Entity\Psiholog')]
    #[ORM\JoinColumn(name: 'psiholog_id', referencedColumnName: 'id')]
    private Psiholog $psiholog;

    #[ORM\OneToOne(targetEntity: '\App\Psiholog\Entity\PsihologPrice')]
    #[ORM\JoinColumn(name: 'psiholog_price_id', referencedColumnName: 'id')]
    private PsihologPrice $psihologPrice;

    public function getId(): int
    {
        return $this->id;
    }

    public function getUserId(): int
    {
        return $this->userId;
    }

    public function setUserId(int $userId): void
    {
        $this->userId = $userId;
    }

    public function getPsihologId(): int
    {
        return $this->psihologId;
    }

    public function setPsihologId(int $psihologId): void
    {
        $this->psihologId = $psihologId;
    }

    public function getPsihologPriceId(): int
    {
        return $this->psihologPriceId;
    }

    public function setPsihologPriceId(int $psihologPriceId): void
    {
        $this->psihologPriceId = $psihologPriceId;
    }

    public function getState(): int
    {
        return $this->state;
    }

    public function setState(int $state): void
    {
        $this->state = $state;
    }

    public function getUser(): User
    {
        return $this->user;
    }

    public function setUser(User $user): void
    {
        $this->user = $user;
    }

    public function getPsiholog(): Psiholog
    {
        return $this->psiholog;
    }

    public function setPsiholog(Psiholog $psiholog): void
    {
        $this->psiholog = $psiholog;
    }

    public function getPsihologPrice(): PsihologPrice
    {
        return $this->psihologPrice;
    }

    public function setPsihologPrice(PsihologPrice $psihologPrice): void
    {
        $this->psihologPrice = $psihologPrice;
    }

    public function getType(): int
    {
        return $this->type;
    }

    public function setType(int $type): void
    {
        $this->type = $type;
    }
}
