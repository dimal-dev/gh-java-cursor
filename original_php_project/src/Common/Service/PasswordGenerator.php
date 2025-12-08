<?php

declare(strict_types=1);

namespace App\Common\Service;

final class PasswordGenerator
{
    public function generate(int $passwordLength = 8): string
    {
        $alphabet = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890';
        $password = [];
        $alphaLength = strlen($alphabet) - 1;
        for ($i = 0; $i < $passwordLength; $i++) {
            $n = rand(0, $alphaLength);
            $password[] = $alphabet[$n];
        }

        return implode($password);
    }
}
