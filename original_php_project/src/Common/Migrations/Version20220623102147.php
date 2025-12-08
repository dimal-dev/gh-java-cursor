<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220623102147 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $sql = <<<'SQL'
ALTER TABLE user ADD COLUMN locale VARCHAR(50) DEFAULT 'ua' AFTER timezone
SQL;

        $this->addSql($sql);
    }

    public function down(Schema $schema): void
    {
        $this->addSql('
ALTER TABLE user DROP COLUMN locale
');
    }
}
