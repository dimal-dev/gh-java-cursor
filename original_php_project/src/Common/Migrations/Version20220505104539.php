<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220505104539 extends AbstractMigration
{
    public function up(Schema $schema) : void
    {
        $this->addSql("ALTER TABLE psiholog_price ADD COLUMN state TINYINT UNSIGNED NOT NULL DEFAULT 2 AFTER type");
    }

    public function down(Schema $schema) : void
    {
        $this->addSql("ALTER TABLE psiholog_price DROP COLUMN state");
    }
}
