<?php

declare(strict_types=1);

namespace App\Billing\Entity;

use App\Psiholog\Entity\PsihologSchedule;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Table(name: 'billing_order_psiholog_schedule')]
#[ORM\Entity]
class OrderPsihologSchedule
{
    #[ORM\Column(name: 'id', type: 'integer', nullable: false, options: ['unsigned' => true])]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'AUTO')]
    private int $id;

    #[ORM\Column(name: 'order_id', type: 'integer')]
    private int $orderId;

    #[ORM\OneToOne(targetEntity: 'Order')]
    #[ORM\JoinColumn(name: 'order_id', referencedColumnName: 'id')]
    private Order $order;

    #[ORM\Column(name: 'psiholog_schedule_id', type: 'integer')]
    private int $psihologScheduleId;

    #[ORM\OneToOne(targetEntity: '\App\Psiholog\Entity\PsihologSchedule')]
    #[ORM\JoinColumn(name: 'psiholog_schedule_id', referencedColumnName: 'id')]
    private PsihologSchedule $psihologSchedule;

    public function getId(): int
    {
        return $this->id;
    }

    public function getOrderId(): int
    {
        return $this->orderId;
    }

    public function setOrderId(int $orderId): void
    {
        $this->orderId = $orderId;
    }

    public function getOrder(): Order
    {
        return $this->order;
    }

    public function setOrder(Order $order): void
    {
        $this->order = $order;
    }

    public function getPsihologScheduleId(): int
    {
        return $this->psihologScheduleId;
    }

    public function setPsihologScheduleId(int $psihologScheduleId): void
    {
        $this->psihologScheduleId = $psihologScheduleId;
    }

    public function getPsihologSchedule(): PsihologSchedule
    {
        return $this->psihologSchedule;
    }

    public function setPsihologSchedule(PsihologSchedule $psihologSchedule): void
    {
        $this->psihologSchedule = $psihologSchedule;
    }
}
