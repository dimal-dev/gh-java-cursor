<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20221103092312 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $sql = <<<'SQL'
create table promocode
(
    id               INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(500)                         NOT NULL,
    state            TINYINT UNSIGNED NOT NULL,
    discount_percent TINYINT UNSIGNED NOT NULL,
    max_use_number   TINYINT UNSIGNED DEFAULT 1,
    expire_at        datetime,
    date_created     DATETIME default current_timestamp() NOT NULL,
    date_modified    DATETIME default current_timestamp() NOT NULL on update current_timestamp (),
    INDEX (date_created),
    UNIQUE (name)
) engine = innodb
  charset = utf8;
SQL;

        $this->addSql($sql);
    }

    public function down(Schema $schema): void
    {
        $this->addSql("DROP TABLE promocode");
    }
}
