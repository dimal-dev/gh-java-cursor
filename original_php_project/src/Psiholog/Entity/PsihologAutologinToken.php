<?php

declare(strict_types=1);

namespace App\Psiholog\Entity;

use Doctrine\ORM\Mapping as ORM;

#[ORM\Table(name: 'psiholog_autologin_token')]
#[ORM\Entity]
class PsihologAutologinToken
{
    #[ORM\Column(name: 'id', type: 'integer', nullable: false, options: ['unsigned' => true])]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'AUTO')]
    private int $id;
    #[ORM\Column(name: 'token', type: 'string', length: 32, nullable: false)]
    private string $token;
    #[ORM\OneToOne(targetEntity: 'Psiholog', inversedBy: 'psihologAutologinToken')]
    #[ORM\JoinColumn(name: 'psiholog_id', referencedColumnName: 'id')]
    private Psiholog $psiholog;
    public function getId(): int
    {
        return $this->id;
    }
    public function getToken(): string
    {
        return $this->token;
    }
    public function setToken(string $token): void
    {
        $this->token = $token;
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
