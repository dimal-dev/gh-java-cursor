<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220427153638 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $sql = <<<'SQL'
ALTER TABLE psiholog_settings CHANGE COLUMN timezone_id timezone VARCHAR(255) NOT NULL AFTER psiholog_id
SQL;

        $this->addSql($sql);
    }

    public function down(Schema $schema): void
    {
        $this->addSql('
ALTER TABLE psiholog_settings CHANGE COLUMN timezone timezone_id tinyint unsigned not null AFTER psiholog_id
');
    }
}
