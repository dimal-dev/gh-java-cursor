<?php

namespace App\Common\Service;

use Symfony\Component\HttpFoundation\Request;
use Symfony\Contracts\HttpClient\HttpClientInterface;
use TheIconic\Tracking\GoogleAnalytics\Analytics;

class GaClient
{
    public function __construct(
        private HttpClientInterface $httpClient,
        private string $gaMeasurementId,
        private string $gaApiSecret,
    ) {
    }

    public function sendPurchase(
        string $clientId,
        string $transactionId,
        string $currency,
        string $price,
        string $itemName,
        string $itemId,
    ): void {
        $url = "https://www.google-analytics.com/mp/collect?measurement_id={$this->gaMeasurementId}&api_secret={$this->gaApiSecret}";

        $body = [
            'client_id' => $clientId,
            'events' => [
                [
                    'name' => 'purchase',
                    'params' => [
                        'currency' => $currency,
                        'transaction_id' => $transactionId,
                        'value' => $price,
                        'items' => [
                            [
//                                'item_id' => $itemId,
//                                'item_name' => $itemName,
                                'id' => $itemId,
                                'name' => $itemName,
                                'currency' => $currency,
                                'price' => $price,
                            ],
                        ],
                    ],
                ],
            ],
        ];

        $bodyJson = json_encode($body);

        /**
         * "transaction_id": "24.031608523954162",
        "affiliation": "Google online store",
        "value": 23.07,
        "currency": "USD",
        "tax": 1.24,
        "shipping": 0,
        "items": [
        {
        "id": "P12345",
        "name": "Android Warhol T-Shirt",
        "list_name": "Search Results",
        "brand": "Google",
        "category": "Apparel/T-Shirts",
        "variant": "Black",
        "list_position": 1,
        "quantity": 2,
        "price": '2.0'
        },
        {
        "id": "P67890",
        "name": "Flame challenge TShirt",
        "list_name": "Search Results",
        "brand": "MyBrand",
        "category": "Apparel/T-Shirts",
        "variant": "Red",
        "list_position": 2,
        "quantity": 1,
        "price": '3.0'
        }
        ]
         */

        $this->httpClient->request(Request::METHOD_POST, $url, [
            'body' => $bodyJson,
        ]);
    }

    public function sendPurchaseUA(
        string $clientId,
        string $transactionId,
        string $currency,
        string $price,
        string $itemName,
        string $itemId,
    ): void {

        $analytics = new Analytics();

        $analytics->setProtocolVersion('1')
            ->setTrackingId('UA-230673385-1')
            ->setClientId($clientId)
            ->setUserId('123');

// Then, include the transaction data
        $analytics->setTransactionId($transactionId)
//            ->setAffiliation('THE ICONIC')
            ->setRevenue($price)
//            ->setTax(25.0)
//            ->setShipping(15.0)
//            ->setCouponCode('MY_COUPON')
        ;

// Include a product
        $productData1 = [
            'sku' => $itemId,
            'name' => $itemName,
//            'brand' => 'Test Brand 2',
//            'category' => 'Test Category 3/Test Category 4',
//            'variant' => 'yellow',
            'price' => $price,
            'quantity' => 1,
//            'coupon_code' => 'TEST 2',
            'position' => 1
        ];

        $analytics->addProduct($productData1);
        $analytics->setProductActionToPurchase();

        $analytics->setEventCategory('ecommerce')
            ->setEventAction('Purchase')
            ->sendEvent();
    }
}
