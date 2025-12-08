<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20220504121620 extends AbstractMigration
{

    public function up(Schema $schema): void
    {
        $this->addSql('CREATE INDEX user_id ON billing_order (user_id)');
    }

    public function down(Schema $schema): void
    {
        $this->addSql('DROP INDEX user_id ON billing_order');
    }
}
