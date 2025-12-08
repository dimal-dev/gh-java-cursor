<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220502104338 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $sql = <<<'SQL'
create table billing_order
(
    id                   INT UNSIGNED                         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    state                TINYINT UNSIGNED                     NOT NULL,
    checkout_slug        char(32)                             not null,
    price                INT                                  NOT NULL,
    currency             VARCHAR(20)                          NOT NULL,
    billing_product_id   INT UNSIGNED                         NOT NULL,
    psiholog_price_id    INT UNSIGNED                         NULL,
    psiholog_schedule_id INT UNSIGNED                         NULL,
    issuer_bank_country  VARCHAR(500),
    issuer_bank_name     VARCHAR(500),
    payment_system        VARCHAR(500),
    phone                VARCHAR(500),
    email                VARCHAR(500),
    client_name          VARCHAR(500),
    card_pan             VARCHAR(500),
    card_type            VARCHAR(500),
    pending_state_at     DATETIME,
    approved_state_at    DATETIME,
    failed_state_at      DATETIME,
    date_created         DATETIME                             NOT NULL,
    date_modified        DATETIME default current_timestamp() NOT NULL on update current_timestamp(),
    INDEX (date_created),
    INDEX (date_modified),
    INDEX (email),
    INDEX (psiholog_price_id),
    INDEX (psiholog_schedule_id)
) engine = innodb collate = utf8mb4_bin;
SQL;

        $this->addSql($sql);
    }

    public function down(Schema $schema): void
    {
        $this->addSql('DROP TABLE billing_order');
    }
}
