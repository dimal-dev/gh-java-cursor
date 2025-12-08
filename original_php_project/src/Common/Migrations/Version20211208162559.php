<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20211208162559 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $sql = <<<'SQL'
RENAME TABLE psiholog_user TO psiholog
SQL;

        $this->addSql($sql);
    }

    public function down(Schema $schema): void
    {
        $this->addSql('
RENAME TABLE psiholog TO psiholog_user
');
    }
}
