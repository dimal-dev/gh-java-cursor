<?php

namespace App\User\Command;

use Doctrine\ORM\EntityManagerInterface;
use Symfony\Component\Console\Command\Command;
use Symfony\Component\Console\Input\InputInterface;
use Symfony\Component\Console\Output\OutputInterface;

class ConsultationCompleteCommand extends Command
{
    public function __construct(
        private EntityManagerInterface $em,
    )
    {
        parent::__construct("user:consultation:complete");
    }

    protected function execute(InputInterface $input, OutputInterface $output): int
    {
//        $qb =

        return 0;
    }

}
