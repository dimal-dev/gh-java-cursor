<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20221019140944 extends AbstractMigration
{
    public function up(Schema $schema) : void
    {
        $this->addSql("alter table billing_order add column billing_checkout_id int unsigned after user_consultation_id");
    }

    public function down(Schema $schema) : void
    {
        $this->addSql("alter table billing_order DROP column billing_checkout_id");
    }
}
