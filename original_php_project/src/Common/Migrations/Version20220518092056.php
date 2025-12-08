<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220518092056 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $this->addSql("DROP TABLE user_consultation_request");
    }

    public function down(Schema $schema): void
    {
        $sql = <<<'SQL'
create table user_consultation_request
(
    id                   INT UNSIGNED                         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_consultation_id INT UNSIGNED                         NOT NULL,
    state                TINYINT UNSIGNED                     NOT NULL,
    decline_type         TINYINT UNSIGNED,
    decline_reason       LONGTEXT DEFAULT '',
    accepted_at          datetime,
    declined_at          datetime,
    date_created         DATETIME default current_timestamp() NOT NULL,
    date_modified        DATETIME default current_timestamp() NOT NULL on update current_timestamp(),
    INDEX (date_created),
    INDEX (user_consultation_id)
) engine = innodb
  charset = utf8;
SQL;

        $this->addSql($sql);
    }
}
