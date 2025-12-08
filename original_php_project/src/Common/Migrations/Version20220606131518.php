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
final class Version20220606131518 extends AbstractMigration
{
    private const PSIHOLOG_LIST = [
        [
            'id' => Psiholog::PSIHOLOG_ID_NATALYA_PONOMAREVA,
            'email' => 'natalka4321@ukr.net',
            'first_name' => 'Наталья',
            'last_name' => 'Пономарева',
            'birth_date' => '1981-08-28 00:00:00',
            'works_from' => '2012-01-01 00:00:00',
            'profile_template' => '012-natalya-ponomareva',
            'price' => '1500',
            'pay_rate_percent' => '50',
            'sex' => 1,
            'timezone' => 'Europe/Zurich',

            'role' => Psiholog::ROLE_PSIHOLOG,
            'state' => Psiholog::STATE_ACTIVE,
            'price_currency' => Currency::UAH,
            'price_type' => PsihologPrice::TYPE_INDIVIDUAL,
            'price_state' => PsihologPrice::STATE_CURRENT,
        ],
    ];

    public function up(Schema $schema): void
    {
        foreach (self::PSIHOLOG_LIST as $config) {
            $sql = <<<"SQL"
    INSERT INTO psiholog (id, email, role, state) 
    VALUES 
    (
     {$config['id']}, '{$config['email']}', {$config['role']}, {$config['state']}
     )
SQL;

            $this->addSql($sql);

            $token = Uuid::uuid4()->getHex();
            $sql = <<<"SQL"
    INSERT INTO psiholog_autologin_token (id, psiholog_id, token) 
    VALUES 
    (
     {$config['id']}, {$config['id']}, '$token'
     )
    SQL;
            $this->addSql($sql);

            $sql = <<<"SQL"
    INSERT INTO psiholog_price (id, psiholog_id, price, currency, type, state, pay_rate_percent) 
    VALUES 
    (
     {$config['id']}, {$config['id']}, {$config['price']}, '{$config['price_currency']}', {$config['price_type']}, {$config['price_state']}, {$config['pay_rate_percent']}
     )
    SQL;
            $this->addSql($sql);

            $sql = <<<"SQL"
    INSERT INTO psiholog_profile (id, psiholog_id, first_name, last_name, birth_date, works_from, profile_template, sex) 
    VALUES 
    (
     {$config['id']}, {$config['id']}, '{$config['first_name']}', '{$config['last_name']}', '{$config['birth_date']}', '{$config['works_from']}', '{$config['profile_template']}', {$config['sex']}
     )
    SQL;
            $this->addSql($sql);

            $sql = <<<"SQL"
    INSERT INTO psiholog_settings (id, psiholog_id, timezone) 
    VALUES 
    (
     {$config['id']}, {$config['id']}, '{$config['timezone']}'
     )
    SQL;
            $this->addSql($sql);

        }
    }

    public function down(Schema $schema): void
    {
        $tables = [
            'psiholog',
            'psiholog_settings',
            'psiholog_profile',
            'psiholog_price',
            'psiholog_autologin_token',
        ];
        foreach (self::PSIHOLOG_LIST as $psiholog) {
            $id = $psiholog['id'];
            foreach ($tables as $table) {
                $sql = "DELETE FROM {$table} WHERE id = {$id}";

                $this->addSql($sql);
            }
        }

    }
}
