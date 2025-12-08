<?php

namespace App\Notification\Command;

use Symfony\Component\Console\Command\Command;
use Symfony\Component\Console\Input\InputInterface;
use Symfony\Component\Console\Output\OutputInterface;
use Telegram\Bot\Api;

class NotifyTelegramCommand extends Command
{
    public function __construct(
        private Api $telegramClient
    )
    {
        parent::__construct("notification:telegram:user");
    }

    protected function execute(InputInterface $input, OutputInterface $output): int
    {
//        $params = [
//     *   'url'         => '',
//     *   'certificate' => '',
//     *   'max_connections' => '',
//     *   'allowed_updates' => '',
//     * ];

        $params = [
            'url' => 'https://goodhelp.com.ua/notification/telegram/webhook',
            'max_connections' => 2,
            'allowed_updates' => ["message"]
        ];
        dd($this->telegramClient->setWebhook($params));
        $this->telegramClient->setWebhook($params);

        $chatId = 367485174;

        $message = <<<'MSG'
У вас 5 новых сообщений в чате от клиентов.

<a href="http://www.example.com/">Зайти посмотреть</a>
MSG;

        $params = [
            'chat_id' => $chatId,
            'text' => $message,
            'parse_mode' => 'html',
        ];

        dd($this->telegramClient->sendMessage($params));


//        * $params = [
//     *   'chat_id'                  => '',
//     *   'text'                     => '',
//     *   'parse_mode'               => '',
//     *   'disable_web_page_preview' => '',
//     *   'disable_notification'     => '',
//     *   'reply_to_message_id'      => '',
//     *   'reply_markup'             => '',
//     * ];
//        dd($this->telegramClient->getWebhookInfo());
        $updates = $this->telegramClient->getUpdates();
        dd($updates);

        return 0;
    }

}
