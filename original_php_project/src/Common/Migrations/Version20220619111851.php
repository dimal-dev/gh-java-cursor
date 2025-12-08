<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use App\Billing\Entity\Currency;
use App\Psiholog\Entity\Psiholog;
use App\Psiholog\Entity\PsihologPrice;
use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;
use Ramsey\Uuid\Uuid;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220619111851 extends AbstractMigration
{
    private const PSIHOLOG_LIST = [
        [
            'id' => Psiholog::PSIHOLOG_ID_ZERO_MAN,
            'price' => '2',
            'price_currency' => Currency::UAH,
            'pay_rate_percent' => '97.8',
            'price_type' => PsihologPrice::TYPE_COUPLE,
            'price_state' => PsihologPrice::STATE_CURRENT,
        ],
        [
            'id' => Psiholog::PSIHOLOG_ID_ALISA_CHIZIKOVA,
            'price' => '3000',
            'price_currency' => Currency::UAH,
            'pay_rate_percent' => '90',
            'price_type' => PsihologPrice::TYPE_COUPLE,
            'price_state' => PsihologPrice::STATE_CURRENT,
        ],
        [
            'id' => Psiholog::PSIHOLOG_ID_INNA_KARASHCHUK,
            'price' => '2250',
            'price_currency' => Currency::UAH,
            'pay_rate_percent' => '40',
            'price_type' => PsihologPrice::TYPE_COUPLE,
            'price_state' => PsihologPrice::STATE_CURRENT,
        ],
        [
            'id' => Psiholog::PSIHOLOG_ID_VIKTORIYA_RADIY,
            'price' => '1500',
            'price_currency' => Currency::UAH,
            'pay_rate_percent' => '40',
            'price_type' => PsihologPrice::TYPE_COUPLE,
            'price_state' => PsihologPrice::STATE_CURRENT,
        ],
        [
            'id' => Psiholog::PSIHOLOG_ID_OKSANA_RYZHIKOVA,
            'price' => '1500',
            'price_currency' => Currency::UAH,
            'pay_rate_percent' => '40',
            'price_type' => PsihologPrice::TYPE_COUPLE,
            'price_state' => PsihologPrice::STATE_CURRENT,
        ],
        [
            'id' => Psiholog::PSIHOLOG_ID_VALENTINA_DOVBYSH,
            'price' => '1500',
            'price_currency' => Currency::UAH,
            'pay_rate_percent' => '40',
            'price_type' => PsihologPrice::TYPE_COUPLE,
            'price_state' => PsihologPrice::STATE_CURRENT,
        ],
        [
            'id' => Psiholog::PSIHOLOG_ID_YULIYA_GURNEVICH,
            'price' => '2250',
            'price_currency' => Currency::UAH,
            'pay_rate_percent' => '40',
            'price_type' => PsihologPrice::TYPE_COUPLE,
            'price_state' => PsihologPrice::STATE_CURRENT,
        ],
        [
            'id' => Psiholog::PSIHOLOG_ID_VIKTOR_DICHKO,
            'price' => '2250',
            'price_currency' => Currency::UAH,
            'pay_rate_percent' => '40',
            'price_type' => PsihologPrice::TYPE_COUPLE,
            'price_state' => PsihologPrice::STATE_CURRENT,
        ],
        [
            'id' => Psiholog::PSIHOLOG_ID_MARIYA_KOROYED,
            'price' => '2250',
            'price_currency' => Currency::UAH,
            'pay_rate_percent' => '40',
            'price_type' => PsihologPrice::TYPE_COUPLE,
            'price_state' => PsihologPrice::STATE_CURRENT,
        ],
        [
            'id' => Psiholog::PSIHOLOG_ID_SAN_SATTARI,
            'price' => '2250',
            'price_currency' => Currency::UAH,
            'pay_rate_percent' => '50',
            'price_type' => PsihologPrice::TYPE_COUPLE,
            'price_state' => PsihologPrice::STATE_CURRENT,
        ],
        [
            'id' => Psiholog::PSIHOLOG_ID_ARTEM_KHMELEVSKIY,
            'price' => '2250',
            'price_currency' => Currency::UAH,
            'pay_rate_percent' => '50',
            'price_type' => PsihologPrice::TYPE_COUPLE,
            'price_state' => PsihologPrice::STATE_CURRENT,
        ],
        [
            'id' => Psiholog::PSIHOLOG_ID_NATALYA_PONOMAREVA,
            'price' => '2250',
            'price_currency' => Currency::UAH,
            'pay_rate_percent' => '50',
            'price_type' => PsihologPrice::TYPE_COUPLE,
            'price_state' => PsihologPrice::STATE_CURRENT,
        ],
    ];

    public function up(Schema $schema) : void
    {

        foreach (self::PSIHOLOG_LIST as $config) {
            $sql = <<<"SQL"
    INSERT INTO psiholog_price (psiholog_id, price, currency, type, state, pay_rate_percent) 
    VALUES 
    (
     {$config['id']}, {$config['price']}, '{$config['price_currency']}', {$config['price_type']}, {$config['price_state']}, {$config['pay_rate_percent']}
     )
    SQL;
            $this->addSql($sql);
        }
    }

    public function down(Schema $schema) : void
    {
        $tables = [
            'psiholog_price',
        ];
        foreach (self::PSIHOLOG_LIST as $psiholog) {
            $id = $psiholog['id'];
            foreach ($tables as $table) {
                $sql = "DELETE FROM {$table} WHERE psiholog_id = {$id} and price = {$psiholog['price']} and currency = '{$psiholog['price_currency']}' and type = '{$psiholog['price_type']}'";

                $this->addSql($sql);
            }
        }
    }
}
