<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220525132724 extends AbstractMigration
{
    public function up(Schema $schema) : void
    {
        $this->addSql("DROP INDEX psiholog_id ON psiholog_schedule");
    }

    public function down(Schema $schema) : void
    {
        $this->addSql("CREATE INDEX psiholog_id ON psiholog_schedule(psiholog_id)");
    }
}
