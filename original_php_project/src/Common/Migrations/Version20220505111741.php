<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220505111741 extends AbstractMigration
{
    public function up(Schema $schema) : void
    {
        $this->addSql("ALTER TABLE billing_order DROP COLUMN psiholog_schedule_id");
    }

    public function down(Schema $schema) : void
    {
        $this->addSql("ALTER TABLE user ADD COLUMN psiholog_schedule_id INT UNSIGNED NULL AFTER psiholog_price_id");
    }
}
