<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220208105540 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $sql = <<<'SQL'
create table blog_post_tag (
    id int unsigned auto_increment not null primary key,
    blog_post_id int unsigned not null,
    blog_tag_id int unsigned not null,
    date_created  datetime         default current_timestamp() not null,
    index(date_created),
    index(blog_post_id),
    index(blog_tag_id)
) engine=innodb;
SQL;

        $this->addSql($sql);
    }

    public function down(Schema $schema): void
    {
        $this->addSql('DROP TABLE blog_post_tag');
    }
}
