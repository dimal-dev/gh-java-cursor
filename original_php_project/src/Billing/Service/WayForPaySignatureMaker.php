<?php

namespace App\Billing\Service;

use JetBrains\PhpStorm\Pure;

class WayForPaySignatureMaker
{
    public function __construct(
        private string $wayForPayMerchantSecretKey
    ) {
    }

    #[Pure] public function make(array $content, array $fields): string
    {
        $signData = [];
        foreach ($fields as $field) {
            $signData[] = $content[$field] ?? "";
        }
        $signStr = implode(";", $signData);

        return $this->sign($signStr);
    }

    private function sign(string $params): string
    {
        return hash_hmac("md5", $params, $this->wayForPayMerchantSecretKey);
    }
}
