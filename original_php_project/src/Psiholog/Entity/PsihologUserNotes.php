<?php

declare(strict_types=1);

namespace App\Psiholog\Entity;

use App\User\Entity\User;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Table(name: 'psiholog_user_notes')]
#[ORM\Entity]
class PsihologUserNotes
{
    #[ORM\Column(name: 'id', type: 'integer', nullable: false, options: ['unsigned' => true])]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'AUTO')]
    private int $id;

    #[ORM\Column(name: 'name', type: 'string')]
    private string $name;

    #[ORM\Column(name: 'psiholog_id', type: 'integer', nullable: true)]
    private int $psihologId;

    #[ORM\Column(name: 'user_id', type: 'integer', nullable: true)]
    private int $userId;

    #[ORM\OneToOne(targetEntity: 'Psiholog', cascade: ['persist'])]
    private Psiholog $psiholog;

    public function getId(): int
    {
        return $this->id;
    }

    public function getName(): string
    {
        return $this->name;
    }

    public function setName(string $name): void
    {
        $this->name = $name;
    }

    public function getPsiholog(): Psiholog
    {
        return $this->psiholog;
    }

    public function setPsiholog(Psiholog $psiholog): void
    {
        $this->psiholog = $psiholog;
    }

    public function getPsihologId(): int
    {
        return $this->psihologId;
    }

    public function setPsihologId(int $psihologId): void
    {
        $this->psihologId = $psihologId;
    }

    public function getUserId(): int
    {
        return $this->userId;
    }

    public function setUserId(int $userId): void
    {
        $this->userId = $userId;
    }
}
