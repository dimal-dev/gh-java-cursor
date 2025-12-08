<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220811082139 extends AbstractMigration
{
    public function up(Schema $schema) : void
    {
        $this->addSql("alter table psiholog_payout add column notes text after type;");
    }

    public function down(Schema $schema) : void
    {
        $this->addSql("alter table psiholog_payout DROP column notes");
    }
}
