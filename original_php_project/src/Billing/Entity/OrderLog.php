<?php

declare(strict_types=1);

namespace App\Billing\Entity;

use Doctrine\ORM\Mapping as ORM;

#[ORM\Table(name: 'billing_order_log')]
#[ORM\Entity]
class OrderLog
{
    #[ORM\Column(name: 'id', type: 'integer', nullable: false, options: ['unsigned' => true])]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'AUTO')]
    private int $id;

    #[ORM\Column(name: 'content', type: 'json')]
    private array $content;

    public function getId(): int
    {
        return $this->id;
    }

    public function getContent(): array
    {
        return $this->content;
    }

    public function setContent(array $content): void
    {
        $this->content = $content;
    }
}
