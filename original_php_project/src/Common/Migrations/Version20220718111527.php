<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220718111527 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $sql = <<<'SQL'
create table user_request_psiholog
(
    id                INT UNSIGNED                         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    is_processed      TINYINT UNSIGNED                     NOT NULL,
    name              VARCHAR(500)                         NOT NULL,
    email             VARCHAR(500)                         NOT NULL,
    phone             VARCHAR(500)                         NOT NULL,
    channel           VARCHAR(500)                         NOT NULL,
    problem           LONGTEXT                             NOT NULL,
    consultation_type VARCHAR(500)                         NOT NULL,
    price             VARCHAR(500)                         NOT NULL,
    additional_data   LONGTEXT                             NULL,
    date_created      DATETIME default current_timestamp() NOT NULL,
    date_modified     DATETIME default current_timestamp() NOT NULL on update current_timestamp(),
    INDEX (date_created)
) engine = innodb
  charset utf8;
SQL;

        $this->addSql($sql);
    }

    public function down(Schema $schema): void
    {
        $this->addSql('DROP TABLE user_request_psiholog');
    }
}
