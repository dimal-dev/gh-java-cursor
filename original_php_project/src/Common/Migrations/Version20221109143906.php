<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20221109143906 extends AbstractMigration
{
    public function up(Schema $schema) : void
    {
        $this->addSql("alter table user_request_psiholog add column promocode varchar(500) after price");
    }

    public function down(Schema $schema) : void
    {
        $this->addSql("alter table user_request_psiholog drop column promocode");
    }

    public function isTransactional(): bool
    {
        return false;
    }
}
