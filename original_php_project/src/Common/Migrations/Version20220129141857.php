<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220129141857 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $sql = <<<'SQL'
create table user_session
(
    id            INT UNSIGNED                         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id       INT UNSIGNED                         NOT NULL,
    ps_id         INT UNSIGNED                         NOT NULL,
    state         TINYINT UNSIGNED                     NOT NULL,
    date_created  DATETIME default current_timestamp() NOT NULL,
    date_modified DATETIME default current_timestamp() NOT NULL on update current_timestamp(),
    INDEX (date_created),
    INDEX (user_id),
    INDEX (ps_id)
) engine = innodb
  charset = utf8;
SQL;

        $this->addSql($sql);
    }

    public function down(Schema $schema): void
    {
        $this->addSql('DROP TABLE user_session');
    }
}
