<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220522103321 extends AbstractMigration
{
    public function up(Schema $schema) : void
    {
        $this->addSql("ALTER TABLE psiholog_settings ADD COLUMN telegram_chat_id INT UNSIGNED AFTER timezone");
    }

    public function down(Schema $schema) : void
    {
        $this->addSql("ALTER TABLE psiholog_settings DROP COLUMN telegram_chat_id");
    }
}
