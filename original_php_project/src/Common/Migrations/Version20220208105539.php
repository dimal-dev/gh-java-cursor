<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220208105539 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $sql = <<<'SQL'
create table blog_tag (
    id int unsigned auto_increment not null primary key,
    name varchar(1000) not null,
    date_created  datetime         default current_timestamp() not null,
    date_modified datetime         default current_timestamp() not null on update current_timestamp(),
    index(date_created)
) engine=innodb collate utf8mb4_bin;
SQL;

        $this->addSql($sql);
    }

    public function down(Schema $schema): void
    {
        $this->addSql('DROP TABLE blog_tag');
    }
}
