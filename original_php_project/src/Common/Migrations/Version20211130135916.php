<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20211130135916 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $sql = <<<'SQL'
create table staff_user_autologin_token (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    staff_user_id INT UNSIGNED not null,
    token char(32) not null,
    date_created  datetime default current_timestamp() not null,
    INDEX (date_created),
    UNIQUE (token),
    INDEX(staff_user_id)
) engine=innodb charset=utf8;
SQL;

        $this->addSql($sql);
    }

    public function down(Schema $schema): void
    {
        $this->addSql('DROP TABLE staff_user_autologin_token');
    }
}
