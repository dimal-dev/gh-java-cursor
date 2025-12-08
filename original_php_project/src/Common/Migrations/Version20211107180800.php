<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20211107180800 extends AbstractMigration
{
    public function up(Schema $schema) : void
    {
        $sql = <<<'SQL'
create table user (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    email varchar(500) not null,
    date_created  datetime   default CURRENT_TIMESTAMP not null,
    date_modified datetime   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    unique(email),
    key(date_created)
) engine=innodb collate = utf8mb4_bin;
SQL;
        $this->addSql($sql);
    }

    public function down(Schema $schema) : void
    {
        $this->addSql("DROP TABLE user");
    }
}
