<?php

declare(strict_types=1);

namespace App\User\Entity;

use App\Psiholog\Entity\PsihologSchedule;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Table(name: 'user_consultation_psiholog_schedule')]
#[ORM\Entity]
class UserConsultationPsihologSchedule
{
    #[ORM\Column(name: 'id', type: 'integer', nullable: false, options: ['unsigned' => true])]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'AUTO')]
    private int $id;

    #[ORM\Column(name: 'user_consultation_id', type: 'integer')]
    private int $userConsultationId;

    #[ORM\OneToOne(targetEntity: '\App\User\Entity\UserConsultation')]
    #[ORM\JoinColumn(name: 'user_consultation_id', referencedColumnName: 'id')]
    private UserConsultation $userConsultation;

    #[ORM\Column(name: 'psiholog_schedule_id', type: 'integer')]
    private int $psihologScheduleId;

    #[ORM\OneToOne(targetEntity: '\App\Psiholog\Entity\PsihologSchedule')]
    #[ORM\JoinColumn(name: 'psiholog_schedule_id', referencedColumnName: 'id')]
    private PsihologSchedule $psihologSchedule;

    public function getId(): int
    {
        return $this->id;
    }

    public function getUserConsultationId(): int
    {
        return $this->userConsultationId;
    }

    public function setUserConsultationId(int $userConsultationId): void
    {
        $this->userConsultationId = $userConsultationId;
    }

    public function getUserConsultation(): UserConsultation
    {
        return $this->userConsultation;
    }

    public function setUserConsultation(UserConsultation $userConsultation): void
    {
        $this->userConsultation = $userConsultation;
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
