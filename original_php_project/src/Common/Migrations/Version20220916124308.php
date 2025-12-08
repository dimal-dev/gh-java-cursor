<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220916124308 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $sql = <<<'SQL'
create table billing_checkout_psiholog_schedule
(
    id                INT UNSIGNED   NOT NULL AUTO_INCREMENT PRIMARY KEY,
    billing_checkout_id INT UNSIGNED NOT NULL,
    psiholog_schedule_id int unsigned                         not null,
    date_created      DATETIME default current_timestamp() NOT NULL,
    date_modified     DATETIME default current_timestamp() NOT NULL on update current_timestamp(),
    INDEX (billing_checkout_id),
    INDEX (psiholog_schedule_id),
    INDEX (date_created)
) engine = innodb
SQL;

        $this->addSql($sql);
    }

    public function down(Schema $schema): void
    {
        $this->addSql('DROP TABLE billing_checkout_psiholog_schedule');
    }
}
