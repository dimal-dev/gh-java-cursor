<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220518091604 extends AbstractMigration
{
    public function up(Schema $schema) : void
    {
        $this->addSql("ALTER TABLE billing_order ADD COLUMN user_consultation_id INT UNSIGNED AFTER user_id");
    }

    public function down(Schema $schema) : void
    {
        $this->addSql("ALTER TABLE billing_order DROP COLUMN user_consultation_id");
    }
}
