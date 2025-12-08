<?php

declare(strict_types=1);

namespace App\User\Entity;

use App\Psiholog\Entity\Psiholog;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Table(name: 'user_request_psiholog')]
#[ORM\Entity]
class UserRequestPsiholog
{
    #[ORM\Column(name: 'id', type: 'integer', nullable: false, options: ['unsigned' => true])]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'AUTO')]
    private int $id;

    #[ORM\Column(name: 'psiholog_id', type: 'integer', nullable: true)]
    private ?int $psihologId;

    #[ORM\Column(name: 'is_processed', type: 'integer', nullable: false)]
    private int $isProcessed = 0;

    #[ORM\Column(name: 'lgbtq', type: 'integer', nullable: true)]
    private int $lgbtq = 0;

    #[ORM\Column(name: 'name', type: 'string', nullable: false)]
    private string $name;

    #[ORM\Column(name: 'email', type: 'string', nullable: false)]
    private string $email;

    #[ORM\Column(name: 'phone', type: 'string', nullable: false)]
    private string $phone;

    #[ORM\Column(name: 'channel', type: 'string', nullable: false)]
    private string $channel;

    #[ORM\Column(name: 'problem', type: 'string', nullable: false)]
    private string $problem;

    #[ORM\Column(name: 'consultation_type', type: 'string', nullable: false)]
    private string $consultationType;

    #[ORM\Column(name: 'sex', type: 'string', nullable: false)]
    private string $sex = 'both';

    #[ORM\Column(name: 'price', type: 'string', nullable: false)]
    private string $price;

    #[ORM\Column(name: 'promocode', type: 'string')]
    private ?string $promocode;

    #[ORM\Column(name: 'additional_data', type: 'json', nullable: false)]
    private array $additionalData = [];

    public function getId(): int
    {
        return $this->id;
    }

    public function getIsProcessed(): int
    {
        return $this->isProcessed;
    }

    public function setIsProcessed(int $isProcessed): void
    {
        $this->isProcessed = $isProcessed;
    }

    public function getName(): string
    {
        return $this->name;
    }

    public function setName(string $name): void
    {
        $this->name = $name;
    }

    public function getEmail(): string
    {
        return $this->email;
    }

    public function setEmail(string $email): void
    {
        $this->email = $email;
    }

    public function getPhone(): string
    {
        return $this->phone;
    }

    public function setPhone(string $phone): void
    {
        $this->phone = $phone;
    }

    public function getChannel(): string
    {
        return $this->channel;
    }

    public function setChannel(string $channel): void
    {
        $this->channel = $channel;
    }

    public function getProblem(): string
    {
        return $this->problem;
    }

    public function setProblem(string $problem): void
    {
        $this->problem = $problem;
    }

    public function getAdditionalData(): array
    {
        return $this->additionalData;
    }

    public function setAdditionalData(array $additionalData): void
    {
        $this->additionalData = $additionalData;
    }

    public function getConsultationType(): string
    {
        return $this->consultationType;
    }

    public function setConsultationType(string $consultationType): void
    {
        $this->consultationType = $consultationType;
    }

    public function getPrice(): string
    {
        return $this->price;
    }

    public function setPrice(string $price): void
    {
        $this->price = $price;
    }

    public function getSex(): string
    {
        return $this->sex;
    }

    public function setSex(string $sex): void
    {
        $this->sex = $sex;
    }

    public function getPsihologId(): ?int
    {
        return $this->psihologId;
    }

    public function setPsihologId(?int $psihologId): void
    {
        $this->psihologId = $psihologId;
    }

    public function getLgbtq(): int
    {
        return $this->lgbtq;
    }

    public function setLgbtq(int $lgbtq): void
    {
        $this->lgbtq = $lgbtq;
    }

    public function getPromocode(): ?string
    {
        return $this->promocode;
    }

    public function setPromocode(?string $promocode): void
    {
        $this->promocode = $promocode;
    }
}
