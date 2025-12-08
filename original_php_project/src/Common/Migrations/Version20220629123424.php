<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220629123424 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $sql = <<<'SQL'
create table psiholog_payout_user_consultation
(
    id                   INT UNSIGNED                         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    psiholog_payout_id   INT UNSIGNED                         NOT NULL,
    user_consultation_id INT UNSIGNED                         NOT NULL,
    date_created         DATETIME default current_timestamp() NOT NULL,
    date_modified        DATETIME default current_timestamp() NOT NULL on update current_timestamp(),
    INDEX (date_created),
    INDEX (psiholog_payout_id, user_consultation_id),
    INDEX (user_consultation_id)
) engine = innodb
  charset utf8;
SQL;

        $this->addSql($sql);
    }

    public function down(Schema $schema): void
    {
        $this->addSql('DROP TABLE psiholog_payout_user_consultation');
    }
}
