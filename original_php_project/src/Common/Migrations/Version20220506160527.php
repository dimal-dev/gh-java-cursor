<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220506160527 extends AbstractMigration
{

    public function up(Schema $schema) : void
    {
        $this->addSql("DROP TABLE user_session");
    }

    public function down(Schema $schema) : void
    {
        $this->addSql("SELECT 1");
    }
}
