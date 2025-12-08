<?php

declare(strict_types=1);

namespace App\Billing\Entity;

use App\User\Entity\Promocode;
use App\User\Entity\UserPromocode;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Table(name: 'billing_checkout')]
#[ORM\Entity]
class Checkout
{
    #[ORM\Column(name: 'id', type: 'integer', nullable: false, options: ['unsigned' => true])]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'AUTO')]
    private int $id;

    #[ORM\Column(name: 'psiholog_price_id', type: 'integer')]
    private int $psihologPriceId;

    #[ORM\Column(name: 'psiholog_schedule_id', type: 'integer')]
    private int $psihologScheduleId;

    #[ORM\Column(name: 'user_promocode_id', type: 'integer', nullable: true)]
    private ?int $userPromocodeId;

    #[ORM\Column(name: 'slug', type: 'string', length: 32)]
    private string $slug;

    #[ORM\Column(name: 'user_id', type: 'integer', nullable: true)]
    private ?int $userId;

    #[ORM\Column(name: 'auth_type', type: 'string')]
    private string $authType;

    #[ORM\Column(name: 'phone', type: 'string', nullable: true)]
    private ?string $phone;

    #[ORM\Column(name: 'email', type: 'string', nullable: true)]
    private ?string $email;

    #[ORM\Column(name: 'name', type: 'string', nullable: true)]
    private ?string $name;

    #[ORM\Column(name: 'ga_client_id', type: 'string', length: 50)]
    private ?string $gaClientId = null;

    #[ORM\Column(name: 'ga_client_id_original', type: 'string', length: 100)]
    private ?string $gaClientIdOriginal = null;

    #[ORM\OneToOne(targetEntity: 'App\User\Entity\UserPromocode')]
    #[ORM\JoinColumn(name: 'user_promocode_id', referencedColumnName: 'id')]
    private ?UserPromocode $userPromocode;

    public function getId(): int
    {
        return $this->id;
    }

    public function getPsihologPriceId(): int
    {
        return $this->psihologPriceId;
    }

    public function setPsihologPriceId(int $psihologPriceId): void
    {
        $this->psihologPriceId = $psihologPriceId;
    }

    public function getPsihologScheduleId(): int
    {
        return $this->psihologScheduleId;
    }

    public function setPsihologScheduleId(int $psihologScheduleId): void
    {
        $this->psihologScheduleId = $psihologScheduleId;
    }

    public function getUserPromocodeId(): ?int
    {
        return $this->userPromocodeId;
    }

    public function setUserPromocodeId(?int $userPromocodeId): void
    {
        $this->userPromocodeId = $userPromocodeId;
    }

    public function getSlug(): string
    {
        return $this->slug;
    }

    public function setSlug(string $slug): void
    {
        $this->slug = $slug;
    }

    public function getUserId(): ?int
    {
        return $this->userId;
    }

    public function setUserId(?int $userId): void
    {
        $this->userId = $userId;
    }

    public function getAuthType(): string
    {
        return $this->authType;
    }

    public function setAuthType(string $authType): void
    {
        $this->authType = $authType;
    }

    public function getPhone(): ?string
    {
        return $this->phone;
    }

    public function setPhone(?string $phone): void
    {
        $this->phone = $phone;
    }

    public function getEmail(): ?string
    {
        return $this->email;
    }

    public function setEmail(?string $email): void
    {
        $this->email = $email;
    }

    public function getName(): ?string
    {
        return $this->name;
    }

    public function setName(?string $name): void
    {
        $this->name = $name;
    }

    public function getGaClientId(): ?string
    {
        return $this->gaClientId;
    }

    public function setGaClientId(?string $gaClientId): void
    {
        $this->gaClientId = $gaClientId;
    }

    public function getGaClientIdOriginal(): ?string
    {
        return $this->gaClientIdOriginal;
    }

    public function setGaClientIdOriginal(?string $gaClientIdOriginal): void
    {
        $this->gaClientIdOriginal = $gaClientIdOriginal;
    }

    public function getUserPromocode(): ?UserPromocode
    {
        return $this->userPromocode;
    }

    public function setUserPromocode(?UserPromocode $userPromocode): void
    {
        $this->userPromocode = $userPromocode;
    }
}
