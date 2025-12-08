<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220623172630 extends AbstractMigration
{
    public function up(Schema $schema) : void
    {
        $this->addSql("CREATE INDEX slug ON psiholog_price(slug)");
    }

    public function down(Schema $schema) : void
    {
        $this->addSql("DROP INDEX slug ON psiholog_price");
    }
}
