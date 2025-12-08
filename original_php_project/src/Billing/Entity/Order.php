<?php

declare(strict_types=1);

namespace App\Billing\Entity;

use App\User\Entity\User;
use App\User\Entity\UserConsultation;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Table(name: 'billing_order')]
#[ORM\Entity]
class Order
{
    public const STATE_CREATED = 1;
    public const STATE_PENDING = 2;
    public const STATE_APPROVED = 3;
    public const STATE_FAILED = 4;

    #[ORM\Column(name: 'id', type: 'integer', nullable: false, options: ['unsigned' => true])]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'AUTO')]
    private int $id;

    #[ORM\Column(name: 'user_id', type: 'integer', nullable: true)]
    private ?int $userId;

    #[ORM\Column(name: 'billing_checkout_id', type: 'integer', nullable: true)]
    private ?int $checkoutId;

    #[ORM\ManyToOne(targetEntity: '\App\User\Entity\User')]
    #[ORM\JoinColumn(name: 'user_id', referencedColumnName: 'id')]
    private User $user;

    #[ORM\Column(name: 'state', type: 'integer')]
    private int $state = self::STATE_CREATED;

    #[ORM\Column(name: 'ga_client_id', type: 'string', length: 50)]
    private ?string $gaClientId = null;

    #[ORM\Column(name: 'request_cookies', type: 'string')]
    private ?string $requestCookies = null;

    #[ORM\Column(name: 'checkout_slug', type: 'string', length: 32)]
    private string $checkoutSlug;

    #[ORM\Column(name: 'price', type: 'integer')]
    private int $price;

    #[ORM\Column(name: 'currency', type: 'string')]
    private string $currency;

    #[ORM\Column(name: 'billing_product_id', type: 'integer')]
    private int $billingProductId = 0;

    #[ORM\Column(name: 'psiholog_price_id', type: 'integer', nullable: true)]
    private ?int $psihologPriceId;

    #[ORM\Column(name: 'issuer_bank_country', type: 'string', nullable: true)]
    private ?string $issuerBankCountry;

    #[ORM\Column(name: 'issuer_bank_name', type: 'string', nullable: true)]
    private ?string $issuerBankName;

    #[ORM\Column(name: 'payment_system', type: 'string', nullable: true)]
    private ?string $paymentSystem;

    #[ORM\Column(name: 'phone', type: 'string', nullable: true)]
    private ?string $phone;

    #[ORM\Column(name: 'email', type: 'string', nullable: true)]
    private ?string $email;

    #[ORM\Column(name: 'timezone', type: 'string', nullable: true)]
    private ?string $timezone;

    #[ORM\Column(name: 'locale', type: 'string')]
    private string $locale = 'ua';

    #[ORM\Column(name: 'client_name', type: 'string', nullable: true)]
    private ?string $clientName;

    #[ORM\Column(name: 'card_pan', type: 'string', nullable: true)]
    private ?string $cardPan;

    #[ORM\Column(name: 'card_type', type: 'string', nullable: true)]
    private ?string $cardType;

    #[ORM\Column(name: 'reason', type: 'string', nullable: true)]
    private ?string $reason;

    #[ORM\Column(name: 'reason_code', type: 'string', nullable: true)]
    private ?string $reasonCode;

    #[ORM\Column(name: 'fee', type: 'string', nullable: true)]
    private ?string $fee;

    #[ORM\Column(name: 'pending_state_at', type: 'datetime')]
    private ?\DateTime $pendingStateAt;

    #[ORM\Column(name: 'approved_state_at', type: 'datetime')]
    private ?\DateTime $approvedStateAt;

    #[ORM\Column(name: 'failed_state_at', type: 'datetime')]
    private ?\DateTime $failedStateAt;

    #[ORM\Column(name: 'date_created', type: 'datetime')]
    private \DateTime $dateCreated;

    #[ORM\OneToOne(targetEntity: '\App\User\Entity\UserConsultation')]
    #[ORM\JoinColumn(name: 'user_consultation_id', referencedColumnName: 'id')]
    private ?UserConsultation $userConsultation = null;

    public function __construct()
    {
        $this->dateCreated = new \DateTime();
    }

    public function getId(): int
    {
        return $this->id;
    }

    public function setId(int $id): void
    {
        $this->id = $id;
    }

    public function getUserId(): ?int
    {
        return $this->userId;
    }

    public function setUserId(?int $userId): void
    {
        $this->userId = $userId;
    }

    public function getUser(): User
    {
        return $this->user;
    }

    public function setUser(User $user): void
    {
        $this->user = $user;
    }

    public function getState(): int
    {
        return $this->state;
    }

    public function setState(int $state): void
    {
        $this->state = $state;
    }

    public function getCheckoutSlug(): string
    {
        return $this->checkoutSlug;
    }

    public function setCheckoutSlug(string $checkoutSlug): void
    {
        $this->checkoutSlug = $checkoutSlug;
    }

    public function getPrice(): int
    {
        return $this->price;
    }

    public function getPriceLarge(): int
    {
        return (int) ($this->price * 0.01);
    }

    public function setPrice(int $price): void
    {
        $this->price = $price;
    }

    public function getCurrency(): string
    {
        return $this->currency;
    }

    public function setCurrency(string $currency): void
    {
        $this->currency = $currency;
    }

    public function getPsihologPriceId(): ?int
    {
        return $this->psihologPriceId;
    }

    public function setPsihologPriceId(?int $psihologPriceId): void
    {
        $this->psihologPriceId = $psihologPriceId;
    }

    public function getIssuerBankCountry(): ?string
    {
        return $this->issuerBankCountry;
    }

    public function setIssuerBankCountry(?string $issuerBankCountry): void
    {
        $this->issuerBankCountry = $issuerBankCountry;
    }

    public function getIssuerBankName(): ?string
    {
        return $this->issuerBankName;
    }

    public function setIssuerBankName(?string $issuerBankName): void
    {
        $this->issuerBankName = $issuerBankName;
    }

    public function getPaymentSystem(): ?string
    {
        return $this->paymentSystem;
    }

    public function setPaymentSystem(?string $paymentSystem): void
    {
        $this->paymentSystem = $paymentSystem;
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

    public function getTimezone(): ?string
    {
        return $this->timezone;
    }

    public function setTimezone(?string $timezone): void
    {
        $this->timezone = $timezone;
    }

    public function getClientName(): ?string
    {
        return $this->clientName;
    }

    public function setClientName(?string $clientName): void
    {
        $this->clientName = $clientName;
    }

    public function getCardPan(): ?string
    {
        return $this->cardPan;
    }

    public function setCardPan(?string $cardPan): void
    {
        $this->cardPan = $cardPan;
    }

    public function getCardType(): ?string
    {
        return $this->cardType;
    }

    public function setCardType(?string $cardType): void
    {
        $this->cardType = $cardType;
    }

    public function getReason(): ?string
    {
        return $this->reason;
    }

    public function setReason(?string $reason): void
    {
        $this->reason = $reason;
    }

    public function getReasonCode(): ?string
    {
        return $this->reasonCode;
    }

    public function setReasonCode(?string $reasonCode): void
    {
        $this->reasonCode = $reasonCode;
    }

    public function getFee(): ?string
    {
        return $this->fee;
    }

    public function setFee(?string $fee): void
    {
        $this->fee = $fee;
    }

    public function getPendingStateAt(): ?\DateTime
    {
        return $this->pendingStateAt;
    }

    public function setPendingStateAt(?\DateTime $pendingStateAt): void
    {
        $this->pendingStateAt = $pendingStateAt;
    }

    public function getApprovedStateAt(): ?\DateTime
    {
        return $this->approvedStateAt;
    }

    public function setApprovedStateAt(?\DateTime $approvedStateAt): void
    {
        $this->approvedStateAt = $approvedStateAt;
    }

    public function getFailedStateAt(): ?\DateTime
    {
        return $this->failedStateAt;
    }

    public function setFailedStateAt(?\DateTime $failedStateAt): void
    {
        $this->failedStateAt = $failedStateAt;
    }

    public function getDateCreated(): \DateTime
    {
        return $this->dateCreated;
    }

    public function setDateCreated(\DateTime $dateCreated): void
    {
        $this->dateCreated = $dateCreated;
    }

    public function getUserConsultation(): ?UserConsultation
    {
        return $this->userConsultation;
    }

    public function setUserConsultation(?UserConsultation $userConsultation): void
    {
        $this->userConsultation = $userConsultation;
    }

    public function isApproved(): bool
    {
        return $this->state === self::STATE_APPROVED;
    }

    public function isFailed(): bool
    {
        return $this->state === self::STATE_FAILED;
    }

    public function isPending(): bool
    {
        return $this->state === self::STATE_PENDING;
    }

    public function getGaClientId(): ?string
    {
        return $this->gaClientId;
    }

    public function setGaClientId(?string $gaClientId): void
    {
        $this->gaClientId = $gaClientId;
    }

    public function getLocale(): string
    {
        return $this->locale;
    }

    public function setLocale(string $locale): void
    {
        $this->locale = $locale;
    }

    public function getCheckoutId(): ?int
    {
        return $this->checkoutId;
    }

    public function setCheckoutId(?int $checkoutId): void
    {
        $this->checkoutId = $checkoutId;
    }

    public function getRequestCookies(): ?string
    {
        return $this->requestCookies;
    }

    public function setRequestCookies(?string $requestCookies): void
    {
        $this->requestCookies = $requestCookies;
    }
}
