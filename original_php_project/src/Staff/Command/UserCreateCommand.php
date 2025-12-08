<?php

declare(strict_types=1);

namespace App\Staff\Command;

use App\Common\Service\Twig\TimezoneExtension;
use App\User\Repository\UserRepository;
use App\User\Service\UserCreator;
use Symfony\Component\Console\Command\Command;
use Symfony\Component\Console\Input\InputInterface;
use Symfony\Component\Console\Input\InputOption;
use Symfony\Component\Console\Output\OutputInterface;
use Symfony\Component\Console\Style\SymfonyStyle;

class UserCreateCommand extends Command
{
    private const OPTION_EMAIL = 'email';

    public function __construct(
        private UserRepository $userRepository,
        private UserCreator $userCreator
    ) {
        parent::__construct('staff:user:create');
    }

    protected function configure(): void
    {
        $this->addOption(
            self::OPTION_EMAIL,
            null,
            InputOption::VALUE_REQUIRED,
            'For example: petrovich_i_glina@vo.mgle'
        );
    }

    protected function execute(InputInterface $input, OutputInterface $output): int
    {
        $io = new SymfonyStyle($input, $output);

        $email = $this->getEmail($input);

        $this->userCreator->create($email, TimezoneExtension::DEFAULT_TIMEZONE, true);

        $io->success('Successfully created the user');

        $io->block("Email: " . $email);

        $io->title("Have a nice day");

        return 0;
    }

    private function getEmail(InputInterface $input): string
    {
        $email = (string) ($input->getOption(self::OPTION_EMAIL));
        if (empty($email)) {
            $exceptionMessage = self::OPTION_EMAIL . ' must have a value. For example: petrovich_i_glina@vo.mgle';
            throw new \UnexpectedValueException($exceptionMessage);
        }

        if ($this->userRepository->findByEmail($email)) {
            $exceptionMessage = "User with such email already exists";
            throw new \UnexpectedValueException($exceptionMessage);
        }

        return $email;
    }
}
