<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220916123611 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $sql = <<<'SQL'
create table billing_checkout
(
    id                INT UNSIGNED   NOT NULL AUTO_INCREMENT PRIMARY KEY,
    
    psiholog_schedule_id INT UNSIGNED NOT NULL,
    psiholog_price_id INT UNSIGNED NOT NULL,
    
    promocode_id INT UNSIGNED,
    
    slug CHAR(32) NOT NULL,
    
    user_id INT UNSIGNED,
    
    auth_type VARCHAR(500) NOT NULL,
    email VARCHAR(500),
    name VARCHAR(500),
    phone VARCHAR(500),
    
    ga_client_id         varchar(50)                             null,
    ga_client_id_original         varchar(100)                             null,
    
    date_created      DATETIME default current_timestamp() NOT NULL,
    date_modified     DATETIME default current_timestamp() NOT NULL on update current_timestamp(),
    INDEX (email),
    INDEX (user_id),
    INDEX (phone),
    UNIQUE (slug),
    INDEX (date_created)
) engine = innodb
    collate = utf8mb4_bin
SQL;

        $this->addSql($sql);
    }

    public function down(Schema $schema): void
    {
        $this->addSql('DROP TABLE billing_checkout');
    }
}
