<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220508081452 extends AbstractMigration
{
    public function up(Schema $schema) : void
    {
        $this->addSql("ALTER TABLE psiholog_profile ADD COLUMN sex TINYINT UNSIGNED NOT NULL AFTER profile_template");
    }

    public function down(Schema $schema) : void
    {
        $this->addSql("ALTER TABLE psiholog_profile DROP COLUMN sex");
    }
}
