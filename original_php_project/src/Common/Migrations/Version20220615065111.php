<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220615065111 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $sql = <<<'SQL'
ALTER TABLE psiholog_settings ADD COLUMN schedule_time_cap VARCHAR(500) NOT NULL DEFAULT '+3 hour' AFTER telegram_chat_id
SQL;

        $this->addSql($sql);
    }

    public function down(Schema $schema): void
    {
        $this->addSql('
ALTER TABLE psiholog_schedule DROP COLUMN schedule_time_cap
');
    }
}
