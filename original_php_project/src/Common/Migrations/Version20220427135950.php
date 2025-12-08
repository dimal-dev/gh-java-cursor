<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220427135950 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $sql = <<<'SQL'
ALTER TABLE psiholog_schedule ADD COLUMN state TINYINT unsigned not null DEFAULT 1 AFTER psiholog_id
SQL;

        $this->addSql($sql);
    }

    public function down(Schema $schema): void
    {
        $this->addSql('
ALTER TABLE psiholog_schedule DROP COLUMN state
');
    }
}
