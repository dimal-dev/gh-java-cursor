<?php

declare(strict_types=1);

namespace App\Staff\Command;

use App\Staff\Entity\User;
use App\Staff\Repository\UserRepository;
use App\Staff\Service\UserSignUpPerformer;
use Symfony\Component\Console\Command\Command;
use Symfony\Component\Console\Input\InputInterface;
use Symfony\Component\Console\Input\InputOption;
use Symfony\Component\Console\Output\OutputInterface;
use Symfony\Component\Console\Style\SymfonyStyle;

class StaffUserCreateCommand extends Command
{
    private const OPTION_EMAIL = 'email';
    private const OPTION_PASSWORD = 'password';
    private const OPTION_ROLE = 'role';

    public function __construct(
        private UserRepository $userRepository,
        private UserSignUpPerformer $userCreator
    ) {
        parent::__construct('staff:staff-user:create');
    }

    protected function configure(): void
    {
        $this->addOption(
            self::OPTION_EMAIL,
            null,
            InputOption::VALUE_REQUIRED,
            'For example: petrovich_i_glina@vo.mgle'
        );
        $this->addOption(
            self::OPTION_PASSWORD,
            null,
            InputOption::VALUE_OPTIONAL,
            'Secretus parolus'
        );
        $this->addOption(
            self::OPTION_ROLE,
            null,
            InputOption::VALUE_REQUIRED,
            'See Staff/Entity/User::ROLE_* values'
        );
    }

    protected function execute(InputInterface $input, OutputInterface $output): int
    {
        $io = new SymfonyStyle($input, $output);

        $email = $this->getEmail($input);

        $this->userCreator->signUp($email, $this->getRole($input));

        $io->success('Successfully created the user');
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

    private function getRole(InputInterface $input): int
    {
        $role = (int) ($input->getOption(self::OPTION_ROLE));
        if (empty($role)) {
            $exceptionMessage = self::OPTION_ROLE . ' must have a value';
            throw new \UnexpectedValueException($exceptionMessage);
        }

        if (!in_array($role, User::ROLE_LIST)) {
            $exceptionMessage = self::OPTION_ROLE . ' must be of values: ' . implode(",", User::ROLE_LIST);
            throw new \UnexpectedValueException($exceptionMessage);
        }

        return $role;
    }
}
