<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20221103121544 extends AbstractMigration
{
    public function up(Schema $schema) : void
    {
        $this->addSql("alter table billing_checkout change column promocode_id user_promocode_id int unsigned null");
    }

    public function down(Schema $schema) : void
    {
        $this->addSql("alter table billing_checkout change column user_promocode_id promocode_id int unsigned null");
    }

    public function isTransactional(): bool
    {
        return false;
    }
}
