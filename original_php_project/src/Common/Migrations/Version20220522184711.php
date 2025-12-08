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
final class Version20220522184711 extends AbstractMigration
{
    private const PSIHOLOG_LIST = [
        [
            'id' => Psiholog::PSIHOLOG_ID_ZERO_MAN,
            'email' => 'goodhelpua@gmail.com',
            'role' => Psiholog::ROLE_PSIHOLOG,
            'state' => Psiholog::STATE_ACTIVE,
            'first_name' => 'Зеро',
            'last_name' => 'Мэн',
            'birth_date' => '1999-10-01 00:00:00',
            'works_from' => '2004-04-28 00:00:00',
            'profile_template' => '000-zero-man',
            'price' => '1',
            'price_currency' => Currency::UAH,
            'pay_rate_percent' => '97.8',
            'price_type' => PsihologPrice::TYPE_INDIVIDUAL,
            'price_state' => PsihologPrice::STATE_CURRENT,
            'timezone' => 'Europe/Kiev',
            'sex' => 1,
        ],
        [
            'id' => Psiholog::PSIHOLOG_ID_ALISA_CHIZIKOVA,
            'email' => 'chizhikovaalice@gmail.com',
            'role' => Psiholog::ROLE_PSIHOLOG,
            'state' => Psiholog::STATE_ACTIVE,
            'first_name' => 'Алиса',
            'last_name' => 'Чижикова',
            'birth_date' => '1985-04-28 00:00:00',
            'works_from' => '2004-04-28 00:00:00',
            'profile_template' => '001-alisa-chizikova',
            'price' => '2000',
            'price_currency' => Currency::UAH,
            'pay_rate_percent' => '90',
            'price_type' => PsihologPrice::TYPE_INDIVIDUAL,
            'price_state' => PsihologPrice::STATE_CURRENT,
            'timezone' => 'Europe/Zurich',
            'sex' => 1,
        ],
        [
            'id' => Psiholog::PSIHOLOG_ID_INNA_KARASHCHUK,
            'email' => 'karach@bigmir.net',
            'role' => Psiholog::ROLE_PSIHOLOG,
            'state' => Psiholog::STATE_ACTIVE,
            'first_name' => 'Инна',
            'last_name' => 'Каращук',
            'birth_date' => '1975-05-10 00:00:00',
            'works_from' => '2011-01-01 00:00:00',
            'profile_template' => '002-inna-karashchuk',
            'price' => '1500',
            'price_currency' => Currency::UAH,
            'pay_rate_percent' => '40',
            'price_type' => PsihologPrice::TYPE_INDIVIDUAL,
            'price_state' => PsihologPrice::STATE_CURRENT,
            'timezone' => 'Europe/Zurich',
            'sex' => 1,
        ],
        [
            'id' => Psiholog::PSIHOLOG_ID_VIKTORIYA_RADIY,
            'email' => 'viktoriaradiy@gmail.com',
            'role' => Psiholog::ROLE_PSIHOLOG,
            'state' => Psiholog::STATE_ACTIVE,
            'first_name' => 'Виктория',
            'last_name' => 'Радий',
            'birth_date' => '1988-09-21 00:00:00',
            'works_from' => '2015-01-01 00:00:00',
            'profile_template' => '003-viktoriya-radiy',
            'price' => '1000',
            'price_currency' => Currency::UAH,
            'pay_rate_percent' => '40',
            'price_type' => PsihologPrice::TYPE_INDIVIDUAL,
            'price_state' => PsihologPrice::STATE_CURRENT,
            'timezone' => 'Europe/Zurich',
            'sex' => 1,
        ],
        [
            'id' => Psiholog::PSIHOLOG_ID_OKSANA_RYZHIKOVA,
            'email' => 'ovryzhikova@gmail.com',
            'role' => Psiholog::ROLE_PSIHOLOG,
            'state' => Psiholog::STATE_ACTIVE,
            'first_name' => 'Оксана',
            'last_name' => 'Рыжикова',
            'birth_date' => '1968-05-04 00:00:00',
            'works_from' => '2011-01-01 00:00:00',
            'profile_template' => '004-oksana-ryzhikova',
            'price' => '1000',
            'price_currency' => Currency::UAH,
            'pay_rate_percent' => '40',
            'price_type' => PsihologPrice::TYPE_INDIVIDUAL,
            'price_state' => PsihologPrice::STATE_CURRENT,
            'timezone' => 'Europe/Zurich',
            'sex' => 1,
        ],
        [
            'id' => Psiholog::PSIHOLOG_ID_VALENTINA_DOVBYSH,
            'email' => 'design.zone.18@gmail.com',
            'role' => Psiholog::ROLE_PSIHOLOG,
            'state' => Psiholog::STATE_ACTIVE,
            'first_name' => 'Валентина',
            'last_name' => 'Довбыш',
            'birth_date' => '1975-05-18 00:00:00',
            'works_from' => '2017-01-01 00:00:00',
            'profile_template' => '005-valentina-dovbysh',
            'price' => '1000',
            'price_currency' => Currency::UAH,
            'pay_rate_percent' => '40',
            'price_type' => PsihologPrice::TYPE_INDIVIDUAL,
            'price_state' => PsihologPrice::STATE_CURRENT,
            'timezone' => 'Europe/Zurich',
            'sex' => 1,
        ],
        [
            'id' => Psiholog::PSIHOLOG_ID_YULIYA_GURNEVICH,
            'email' => 'gurnevich.juliya@gmail.com',
            'role' => Psiholog::ROLE_PSIHOLOG,
            'state' => Psiholog::STATE_ACTIVE,
            'first_name' => 'Юлия',
            'last_name' => 'Гурневич',
            'birth_date' => '1984-01-01 00:00:00',
            'works_from' => '2007-01-01 00:00:00',
            'profile_template' => '006-yuliya-gurnevich',
            'price' => '1500',
            'price_currency' => Currency::UAH,
            'pay_rate_percent' => '40',
            'price_type' => PsihologPrice::TYPE_INDIVIDUAL,
            'price_state' => PsihologPrice::STATE_CURRENT,
            'timezone' => 'Europe/Zurich',
            'sex' => 1,
        ],
        [
            'id' => Psiholog::PSIHOLOG_ID_VIKTOR_DICHKO,
            'email' => 'dichkovn@gmail.com',
            'role' => Psiholog::ROLE_PSIHOLOG,
            'state' => Psiholog::STATE_ACTIVE,
            'first_name' => 'Виктор',
            'last_name' => 'Дичко',
            'birth_date' => '1963-01-12 00:00:00',
            'works_from' => '2010-01-01 00:00:00',
            'profile_template' => '007-viktor-dichko',
            'price' => '1500',
            'price_currency' => Currency::UAH,
            'pay_rate_percent' => '40',
            'price_type' => PsihologPrice::TYPE_INDIVIDUAL,
            'price_state' => PsihologPrice::STATE_CURRENT,
            'timezone' => 'Europe/Zurich',
            'sex' => 2,
        ],
        [
            'id' => Psiholog::PSIHOLOG_ID_MARIYA_KOROYED,
            'email' => 'koroed38@gmail.com',
            'role' => Psiholog::ROLE_PSIHOLOG,
            'state' => Psiholog::STATE_ACTIVE,
            'first_name' => 'Мария',
            'last_name' => 'Короед',
            'birth_date' => '1986-01-02 00:00:00',
            'works_from' => '2018-01-01 00:00:00',
            'profile_template' => '008-mariya-koroyed',
            'price' => '1500',
            'price_currency' => Currency::UAH,
            'pay_rate_percent' => '40',
            'price_type' => PsihologPrice::TYPE_INDIVIDUAL,
            'price_state' => PsihologPrice::STATE_CURRENT,
            'timezone' => 'Europe/Zurich',
            'sex' => 1,
        ],
        [
            'id' => Psiholog::PSIHOLOG_ID_SAN_SATTARI,
            'email' => 'san.sattari@gmail.com',
            'role' => Psiholog::ROLE_PSIHOLOG,
            'state' => Psiholog::STATE_ACTIVE,
            'first_name' => 'Сан',
            'last_name' => 'Саттари',
            'birth_date' => '1980-04-16 00:00:00',
            'works_from' => '2010-09-01 00:00:00',
            'profile_template' => '009-san-sattari',
            'price' => '1500',
            'price_currency' => Currency::UAH,
            'pay_rate_percent' => '50',
            'price_type' => PsihologPrice::TYPE_INDIVIDUAL,
            'price_state' => PsihologPrice::STATE_CURRENT,
            'timezone' => 'Europe/Zurich',
            'sex' => 2,
        ],
        [
            'id' => Psiholog::PSIHOLOG_ID_ARTEM_KHMELEVSKIY,
            'email' => 'artey41@gmail.com',
            'role' => Psiholog::ROLE_PSIHOLOG,
            'state' => Psiholog::STATE_ACTIVE,
            'first_name' => 'Артем',
            'last_name' => 'Хмелевский',
            'birth_date' => '1973-04-16 00:00:00',
            'works_from' => '2011-12-01 00:00:00',
            'profile_template' => '010-artem-khmelevskiy',
            'price' => '1500',
            'price_currency' => Currency::UAH,
            'pay_rate_percent' => '50',
            'price_type' => PsihologPrice::TYPE_INDIVIDUAL,
            'price_state' => PsihologPrice::STATE_CURRENT,
            'timezone' => 'Europe/Zurich',
            'sex' => 2,
        ],
        [
            'id' => Psiholog::PSIHOLOG_ID_NADEZHDA_TORDIYA,
            'email' => 'ntordiya@yahoo.com',
            'role' => Psiholog::ROLE_PSIHOLOG,
            'state' => Psiholog::STATE_ACTIVE,
            'first_name' => 'Надежда',
            'last_name' => 'Тордия',
            'birth_date' => '1969-11-12 00:00:00',
            'works_from' => '2010-11-01 00:00:00',
            'profile_template' => '011-nadezhda-tordiya',
            'price' => '1500',
            'price_currency' => Currency::UAH,
            'pay_rate_percent' => '50',
            'price_type' => PsihologPrice::TYPE_INDIVIDUAL,
            'price_state' => PsihologPrice::STATE_CURRENT,
            'timezone' => 'Europe/Zurich',
            'sex' => 1,
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
