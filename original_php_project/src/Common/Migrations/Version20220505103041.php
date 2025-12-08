<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220505103041 extends AbstractMigration
{

    public function up(Schema $schema): void
    {
        $this->addSql("DROP TABLE user_settings");
    }

    public function down(Schema $schema): void
    {
        $sql = <<<'SQL'
create table user_settings
(
    id            INT UNSIGNED                         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id       INT UNSIGNED                         NOT NULL,
    timezone_id   TINYINT UNSIGNED                 NOT NULL,
    date_created  DATETIME default current_timestamp() NOT NULL,
    date_modified DATETIME default current_timestamp() NOT NULL on update current_timestamp(),
    INDEX (date_created),
    INDEX (user_id)
) engine = innodb
  charset = utf8;
SQL;

        $this->addSql($sql);
    }
}
