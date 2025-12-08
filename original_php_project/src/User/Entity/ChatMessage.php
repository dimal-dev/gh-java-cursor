<?php

declare(strict_types=1);

namespace App\User\Entity;

use App\Psiholog\Entity\Psiholog;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Table(name: 'chat_message')]
#[ORM\Entity]
class ChatMessage
{
    public const TYPE_SENT_BY_USER = 1;
    public const TYPE_SENT_BY_THERAPIST = 2;
    public const STATE_UNREAD = 1;
    public const STATE_READ = 2;

    #[ORM\Column(name: 'id', type: 'integer', nullable: false, options: ['unsigned' => true])]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'AUTO')]
    private int $id;

    #[ORM\Column(name: 'type', type: 'integer', nullable: false)]
    private int $type;

    #[ORM\Column(name: 'state', type: 'integer', nullable: false)]
    private int $state = self::STATE_UNREAD;

    #[ORM\Column(name: 'body', type: 'text', nullable: false)]
    private string $body;

    #[ORM\Column(name: 'sent_at', type: 'datetime', nullable: false)]
    private \DateTime $sentAt;

    #[ORM\OneToOne(targetEntity: 'App\User\Entity\User', inversedBy: 'userPsiholog', cascade: ['persist'])]
    private User $user;

    #[ORM\OneToOne(targetEntity: 'App\Psiholog\Entity\Psiholog')]
    #[ORM\JoinColumn(name: 'psiholog_id', referencedColumnName: 'id')]
    private Psiholog $psiholog;

    public function getId(): int
    {
        return $this->id;
    }

    public function getType(): int
    {
        return $this->type;
    }

    public function setType(int $type): void
    {
        $this->type = $type;
    }

    public function getState(): int
    {
        return $this->state;
    }

    public function setState(int $state): void
    {
        $this->state = $state;
    }

    public function getBody(): string
    {
        return $this->body;
    }

    public function setBody(string $body): void
    {
        $this->body = $body;
    }

    public function getSentAt(): \DateTime
    {
        return $this->sentAt;
    }

    public function setSentAt(\DateTime $sentAt): void
    {
        $this->sentAt = $sentAt;
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

    public function setSentByUser(): void
    {
        $this->type = self::TYPE_SENT_BY_USER;
    }

    public function setSentByPsiholog(): void
    {
        $this->type = self::TYPE_SENT_BY_THERAPIST;
    }
}
