<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220727120515 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $sql = <<<'SQL'
create table blog_post_ua (
    id            int unsigned auto_increment
        primary key,
    author        varchar(500)                         null,
    psiholog_id   int unsigned                         null,
    header        varchar(1000)                        not null,
    preview       text                                 not null,
    body          longtext                             not null,
    posted_at     datetime                             not null,
    state         tinyint unsigned                     not null,
    slug          varchar(1100)                        not null,
    post_i18n_id  int unsigned,
    date_created  datetime default current_timestamp() not null,
    date_modified datetime default current_timestamp() not null on update current_timestamp(),
    index(psiholog_id),
    index (posted_at),
    index (post_i18n_id),
    index(date_created)
) engine=innodb collate utf8mb4_bin;
SQL;

        $this->addSql($sql);
    }

    public function down(Schema $schema): void
    {
        $this->addSql('DROP TABLE blog_post_ua');
    }

    public function isTransactional(): bool
    {
        return false;
    }
}
