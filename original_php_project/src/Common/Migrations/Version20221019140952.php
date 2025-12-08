<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20221019140952 extends AbstractMigration
{
    public function up(Schema $schema) : void
    {
        $this->addSql("CREATE INDEX billing_checkout_id ON billing_order(billing_checkout_id)");
    }

    public function down(Schema $schema) : void
    {
        $this->addSql("DROP INDEX billing_checkout_id ON billing_order");
    }
}
