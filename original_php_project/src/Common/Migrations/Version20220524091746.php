<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220524091746 extends AbstractMigration
{

    public function up(Schema $schema): void
    {
        $sql = <<<'SQL'
create table psiholog_user_notes
(
    id            INT UNSIGNED                         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    psiholog_id   INT UNSIGNED                         NOT NULL,
    user_id       TINYINT UNSIGNED                     NOT NULL,
    name          VARCHAR(500),
    date_created  DATETIME default current_timestamp() NOT NULL,
    date_modified DATETIME default current_timestamp() NOT NULL on update current_timestamp(),
    INDEX (psiholog_id, user_id),
    INDEX (date_created)
) engine = innodb
  collate = utf8mb4_bin;
SQL;

        $this->addSql($sql);
    }

    public function down(Schema $schema): void
    {
        $this->addSql("DROP TABLE psiholog_user_notes");
    }
}
