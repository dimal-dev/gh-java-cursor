<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use App\Billing\Entity\Currency;
use App\Psiholog\Entity\Psiholog;
use App\Psiholog\Entity\PsihologPrice;
use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220610124257 extends AbstractMigration
{
    private const PRICE_CONFIGURATION = [
        'psiholog_id' => Psiholog::PSIHOLOG_ID_ALISA_CHIZIKOVA,
        'price' => 3000,
        'currency' => Currency::UAH,
        'type' => PsihologPrice::TYPE_COUPLE,
        'state' => PsihologPrice::STATE_CURRENT,
        'pay_rate_percent' => 90,
    ];

    public function up(Schema $schema): void
    {
        $values = self::PRICE_CONFIGURATION;

        $sql = <<<"SQL"
    INSERT INTO psiholog_price (psiholog_id, price, currency, type, state, pay_rate_percent) 
    VALUES 
    (
     {$values['psiholog_id']}, {$values['price']}, '{$values['currency']}', {$values['type']}, {$values['state']}, {$values['pay_rate_percent']}
     )
    SQL;
        $this->addSql($sql);
    }

    public function down(Schema $schema): void
    {
        // this down() migration is auto-generated, please modify it to your needs

    }
}
