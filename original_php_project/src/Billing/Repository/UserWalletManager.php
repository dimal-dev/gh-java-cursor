<?php

namespace App\Billing\Repository;

use App\Billing\Entity\UserWallet;
use App\Billing\Entity\UserWalletOperation;

class UserWalletManager
{
    public function __construct()
    {
    }

    public function applyOperation(UserWallet $userWallet, UserWalletOperation $userWalletOperation): bool
    {
        if ($userWallet->getCurrency() !== $userWalletOperation->getCurrency()) {
            //todo: handle this and convert
            throw new \RuntimeException("Currencies don't match: userWalletId: {$userWallet->getId()}");
        }

        if ($userWalletOperation->isAdd()) {
            $userWallet->setBalance($userWallet->getBalance() + $userWalletOperation->getAmount());

            return true;
        }

        if ($userWalletOperation->isSubtract()) {
            $userWallet->setBalance($userWallet->getBalance() - $userWalletOperation->getAmount());

            return true;
        }

        throw new \RuntimeException("Unknown operation type: userWalletId: {$userWallet->getId()}");
    }
}
