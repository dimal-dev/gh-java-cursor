<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220727111956 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $sql = <<<'SQL'
RENAME TABLE blog_post TO blog_post_ua
SQL;

        $this->addSql($sql);
    }

    public function down(Schema $schema): void
    {
        $sql = <<<'SQL'
RENAME TABLE blog_post_ua TO blog_post
SQL;

        $this->addSql($sql);
    }

    public function isTransactional(): bool
    {
        return false;
    }
}
