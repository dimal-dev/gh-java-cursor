<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220505111606 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $sql = <<<'SQL'
create table billing_order_psiholog_schedule
(
    id                   INT UNSIGNED                         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_id             INT UNSIGNED                         NOT NULL,
    psiholog_schedule_id INT UNSIGNED                         NOT NULL,
    date_created         DATETIME default current_timestamp() NOT NULL,
    INDEX (date_created),
    INDEX (order_id),
    INDEX (psiholog_schedule_id)
) engine = innodb
  charset = utf8;
SQL;

        $this->addSql($sql);
    }

    public function down(Schema $schema): void
    {
        $this->addSql("DROP TABLE billing_order_psiholog_schedule");
    }
}
