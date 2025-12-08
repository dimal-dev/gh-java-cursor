<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220502133219 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $sql = <<<'SQL'
create table billing_order_log
(
    id            INT UNSIGNED                         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    content       LONGTEXT                             NOT NULL,
    date_created  datetime         default current_timestamp() not null,
    date_modified DATETIME default current_timestamp() NOT NULL on update current_timestamp(),
    INDEX (date_created),
    INDEX (date_modified)
) engine = innodb
  charset utf8;
SQL;

        $this->addSql($sql);
    }

    public function down(Schema $schema): void
    {
        $this->addSql('DROP TABLE billing_order_log');
    }
}
