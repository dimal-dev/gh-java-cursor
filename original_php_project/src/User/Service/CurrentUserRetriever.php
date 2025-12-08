<?php

namespace App\User\Service;

use App\User\Entity\SecurityUser;
use App\User\Entity\User;
use Symfony\Component\Security\Core\Security;

class CurrentUserRetriever
{

    public function __construct(
        private Security $security
    )
    {
    }

    public function get(): ?User
    {
        /** @var SecurityUser $user */
        $user = $this->security->getUser();

        return $user?->getUser();
    }
}