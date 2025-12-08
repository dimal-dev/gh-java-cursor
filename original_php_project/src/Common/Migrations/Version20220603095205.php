<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220603095205 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $sql = <<<'SQL'
ALTER TABLE psiholog_settings CHANGE COLUMN telegram_chat_id telegram_chat_id varchar(500) null
SQL;

        $this->addSql($sql);
    }

    public function down(Schema $schema): void
    {
        $this->addSql(
            '
ALTER TABLE psiholog_settings CHANGE COLUMN telegram_chat_id telegram_chat_id int unsigned null
'
        );
    }
}
