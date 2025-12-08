<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220504134342 extends AbstractMigration
{
    public function up(Schema $schema) : void
    {
        $this->addSql("ALTER TABLE user ADD COLUMN is_email_real TINYINT UNSIGNED NOT NULL default 1 AFTER email");
    }

    public function down(Schema $schema) : void
    {
        $this->addSql("ALTER TABLE user DROP COLUMN is_email_real");
    }
}
