<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20211107180812 extends AbstractMigration
{
    public function up(Schema $schema) : void
    {
        $sql = <<<'SQL'
create table psiholog_user (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    email varchar(500) not null,
    role          tinyint unsigned default 0                   not null,
    state         tinyint unsigned default 0                   not null,
    date_created  datetime   default CURRENT_TIMESTAMP not null,
    date_modified datetime   default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    unique(email),
    key(date_created)
) engine=innodb charset utf8;
SQL;
        $this->addSql($sql);
    }

    public function down(Schema $schema) : void
    {
        $this->addSql("DROP TABLE psiholog_user");
    }
}
