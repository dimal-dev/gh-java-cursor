<?php

declare(strict_types=1);

namespace App\User\Entity;

use App\Psiholog\Entity\Psiholog;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Table(name: 'user_psiholog')]
#[ORM\Entity]
class UserPsiholog
{
    #[ORM\Column(name: 'id', type: 'integer', nullable: false, options: ['unsigned' => true])]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'AUTO')]
    private int $id;

    #[ORM\OneToOne(inversedBy: 'userPsiholog', targetEntity: 'App\User\Entity\User', cascade: ['persist'])]
    private User $user;

    #[ORM\OneToOne(targetEntity: 'App\Psiholog\Entity\Psiholog')]
    #[ORM\JoinColumn(name: 'psiholog_id', referencedColumnName: 'id')]
    private Psiholog $psiholog;

    public function getId(): int
    {
        return $this->id;
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
}
