<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220504121314 extends AbstractMigration
{
    public function up(Schema $schema) : void
    {
        $this->addSql("ALTER TABLE billing_order ADD COLUMN user_id INT UNSIGNED AFTER id");
    }

    public function down(Schema $schema) : void
    {
        $this->addSql("ALTER TABLE billing_order DROP COLUMN user_id");
    }
}
