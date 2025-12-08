<?php

namespace App\Notification\Service;

use Telegram\Bot\Api;
use Telegram\Bot\Exceptions\TelegramSDKException;

class TelegramSender
{
    public function __construct(
        private Api $telegramClient,
    ) {
    }

    public function send(string $chatId, string $message): bool
    {
        $params = [
            'chat_id' => $chatId,
            'text' => $message,
            'parse_mode' => 'html',
        ];

        try {
            $this->telegramClient->sendMessage($params);
            return true;
        } catch (TelegramSDKException $e) {
            return false;
        }
    }
}
