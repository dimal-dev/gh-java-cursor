<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220428085530 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $sql = <<<'SQL'
create table psiholog_profile
(
    id                 INT UNSIGNED                         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    psiholog_id        INT UNSIGNED                         NOT NULL,
    first_name         VARCHAR(255)                         NOT NULL,
    last_name          VARCHAR(255)                         NOT NULL,
    birth_date         datetime                             NOT NULL,
    works_from         datetime                             NOT NULL,
    profile_template   VARCHAR(1000)                        NOT NULL,
    date_created       DATETIME default current_timestamp() NOT NULL,
    date_modified      DATETIME default current_timestamp() NOT NULL on update current_timestamp(),
    INDEX (date_created),
    INDEX (psiholog_id)
) engine = innodb
  collate = utf8mb4_bin;
SQL;

        $this->addSql($sql);
    }

    public function down(Schema $schema): void
    {
        $this->addSql('DROP TABLE psiholog_profile');
    }
}
