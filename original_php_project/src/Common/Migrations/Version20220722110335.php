<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220722110335 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $sql = <<<'SQL'
DROP TABLE blog_post_tag
SQL;

        $this->addSql($sql);
    }

    public function down(Schema $schema): void
    {
        $this->addSql('
');
    }

    public function isTransactional(): bool
    {
        return false;
    }
}
