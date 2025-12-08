<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220525132711 extends AbstractMigration
{
    public function up(Schema $schema) : void
    {
        $this->addSql("DROP INDEX available_at ON psiholog_schedule");
    }

    public function down(Schema $schema) : void
    {
        $this->addSql("CREATE INDEX available_at ON psiholog_schedule(available_at)");
    }
}
