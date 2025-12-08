<?php

declare(strict_types=1);

namespace App\Common\Service;

final class ArrayIndexer
{
    public function byKey(array $inputArray, string $key): array
    {
        $outputArray = [];
        foreach ($inputArray as $item) {
            $keyValue = $item[$key];
            $outputArray[$keyValue][] = $item;
        }

        return $outputArray;
    }

    public function byKeyUnique(array $inputArray, string $key): array
    {
        $outputArray = [];
        foreach ($inputArray as $item) {
            $keyValue = $item[$key];
            $outputArray[$keyValue] = $item;
        }

        return $outputArray;
    }
}
