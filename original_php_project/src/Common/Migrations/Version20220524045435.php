<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220524045435 extends AbstractMigration
{
    public function up(Schema $schema) : void
    {
        $this->addSql("ALTER TABLE user ADD COLUMN is_full_name_set_by_user tinyint unsigned default 0 AFTER full_name");
    }

    public function down(Schema $schema) : void
    {
        $this->addSql("ALTER TABLE user DROP COLUMN is_full_name_set_by_user");
    }
}
