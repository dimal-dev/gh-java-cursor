<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20211209122156 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $sql = <<<'SQL'
create table chat_message
(
    id            INT UNSIGNED                         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id       INT UNSIGNED                         NOT NULL,
    psiholog_id  INT UNSIGNED                         NOT NULL,
    type          TINYINT UNSIGNED                     NOT NULL,
    state         TINYINT UNSIGNED                     NOT NULL,
    body          TEXT                                 NOT NULL,
    sent_at       DATETIME                             NOT NULL,
    date_created  DATETIME default current_timestamp() NOT NULL,
    date_modified DATETIME default current_timestamp() NOT NULL on update current_timestamp(),
    INDEX (date_created),
    INDEX (user_id, psiholog_id),
    INDEX (psiholog_id, user_id)
) engine = innodb collate = utf8mb4_bin;
SQL;

        $this->addSql($sql);
    }

    public function down(Schema $schema): void
    {
        $this->addSql('DROP TABLE chat_message');
    }
}
