<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220504133634 extends AbstractMigration
{
    public function up(Schema $schema) : void
    {
        $this->addSql("ALTER TABLE billing_order ADD COLUMN fee VARCHAR(500) AFTER reason_code");
    }

    public function down(Schema $schema) : void
    {
        $this->addSql("ALTER TABLE billing_order DROP COLUMN fee");
    }
}
