<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220802185733 extends AbstractMigration
{
    public function up(Schema $schema) : void
    {
        $this->addSql("ALTER TABLE user_request_psiholog ADD COLUMN sex VARCHAR(50) DEFAULT 'both' AFTER consultation_type");
    }

    public function down(Schema $schema) : void
    {
        $this->addSql("ALTER TABLE user_request_psiholog DROP COLUMN sex");
    }
}
