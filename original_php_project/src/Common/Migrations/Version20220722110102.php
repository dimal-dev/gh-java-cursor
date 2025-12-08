<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220722110102 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $sql = <<<'SQL'
ALTER TABLE blog_post CHANGE COLUMN psiholog_id psiholog_id  int unsigned null AFTER id
SQL;

        $this->addSql($sql);
    }

    public function down(Schema $schema): void
    {
        $this->addSql('
ALTER TABLE blog_post CHANGE COLUMN psiholog_id psiholog_id int unsigned NOT null AFTER id
');
    }
}
