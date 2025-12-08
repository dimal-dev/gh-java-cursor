<?php

namespace App\Common\Service;

class Pluralizer
{
    public static function p(int $amount, string $one, string $few, string $many): string
    {
        if ($amount > 4 && $amount < 20) {
            return $many;
        }

        $amount = $amount % 10;
        if ($amount === 1) {
            return $one;
        }

        if ($amount > 1 && $amount < 5) {
            return $few;
        }

        return $many;
        /**
         * 0 огурцов лет перстней
         * 1 огурец год перстень
         * 2 огурца года перстня
         * 3 огурца года перстня
         * 4 огурца года перстня
         * 5 огурцов лет перстней
         * 20 огурцов лет перстней
         * 21 огурец год перстень
         * 22 огурца года
         * 23 огрца года
         * 24 огурца года
         * 25 огурцов лет
         */
    }
}
