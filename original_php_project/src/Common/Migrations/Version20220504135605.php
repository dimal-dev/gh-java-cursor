<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220504135605 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $sql = <<<'SQL'
create table billing_user_wallet_operation
(
    id            INT UNSIGNED                         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    buw_id        INT                                  NOT NULL,
    amount        INT                                  NOT NULL,
    type          TINYINT                              NOT NULL,
    reason_type   TINYINT                              NOT NULL,
    reason_id     INT                                  NOT NULL,
    date_created  datetime default current_timestamp() not null,
    date_modified DATETIME default current_timestamp() NOT NULL on update current_timestamp(),
    INDEX (date_created),
    INDEX (buw_id),
    INDEX (reason_type, reason_id)
) engine = innodb
  charset = utf8;
SQL;

        $this->addSql($sql);
    }

    public function down(Schema $schema): void
    {
        $this->addSql('DROP TABLE billing_user_wallet_operation');
    }
}
