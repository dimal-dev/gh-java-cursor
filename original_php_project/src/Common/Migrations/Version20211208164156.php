<?php

declare(strict_types=1);

namespace App\Common\Migrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20211208164156 extends AbstractMigration
{
    public function up(Schema $schema): void
    {
        $sql = <<<'SQL'
RENAME TABLE psiholog_user_autologin_token TO psiholog_autologin_token
SQL;

        $this->addSql($sql);
    }

    public function down(Schema $schema): void
    {
        $this->addSql('
RENAME TABLE psiholog_autologin_token TO psiholog_user_autologin_token
');
    }
}
