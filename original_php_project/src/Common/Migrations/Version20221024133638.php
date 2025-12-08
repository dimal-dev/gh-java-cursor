<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20221024133638 extends AbstractMigration
{
    public function up(Schema $schema) : void
    {
        $this->addSql("alter table user_request_psiholog add column lgbtq tinyint unsigned after id");
    }

    public function down(Schema $schema) : void
    {
        $this->addSql("alter table user_request_psiholog DROP column lgbtq");
    }
}
