<?php

namespace App\Psiholog\Service;

use App\Psiholog\Entity\SecurityPsiholog;
use App\Psiholog\Entity\Psiholog;
use Symfony\Component\Security\Core\Security;

class CurrentPsihologRetriever
{

    public function __construct(
        private Security $security
    )
    {
    }

    public function get(): ?Psiholog
    {
        /** @var SecurityPsiholog $psiholog */
        $psiholog = $this->security->getUser();

        return $psiholog?->getPsiholog();
    }
}