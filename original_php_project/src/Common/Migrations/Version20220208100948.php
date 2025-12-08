<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220208100948 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $sql = <<<'SQL'
create table image
(
    id               int unsigned auto_increment
        primary key,
    parent_id        int unsigned                       null,
    storage_provider tinyint unsigned                   not null,
    filepath         varchar(2000)                      not null,
    resolution_type  tinyint unsigned                   not null,
    mimetype         varchar(100)                       not null,
    date_created     datetime default CURRENT_TIMESTAMP not null,
    date_modified    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    key (date_created),
    key (parent_id)
)
    collate = utf8mb4_unicode_ci;
SQL;

        $this->addSql($sql);
    }

    public function down(Schema $schema): void
    {
        $this->addSql('DROP TABLE image');
    }
}
