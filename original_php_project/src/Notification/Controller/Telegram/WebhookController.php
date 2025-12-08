<?php

namespace App\Notification\Controller\Telegram;

use App\Common\Service\TimezoneHelper;
use App\Psiholog\Entity\PsihologSettings;
use App\Psiholog\Repository\PsihologAutologinTokenRepository;
use App\Psiholog\Repository\PsihologSettingsRepository;
use Doctrine\ORM\EntityManagerInterface;
use Psr\Log\LoggerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Telegram\Bot\Api;

#[Route(
    "/telegram/webhook",
    name: "telegram_webhook",
    methods: ["POST"]
)]
final class WebhookController extends AbstractController
{
    public const PSIHOLOG_SETUP_PREFIX = 'z5k_ps_2_p_1_7yofmlwrgbdu53_:';

    public function __construct(
        private LoggerInterface $logger,
        private PsihologAutologinTokenRepository $psihologAutologinTokenRepository,
        private PsihologSettingsRepository $psihologSettingsRepository,
        private EntityManagerInterface $em,
        private Api $telegramClient,
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $body = json_decode($request->getContent(), true);
        if ($body) {
            $this->saveChatId($body);
        }

        $this->logger->info("Azaza", $body);

        return new Response("");
    }

    private function saveChatId(array $body): void
    {
        $chatId = $body['message']['chat']['id'] ?? null;
        $text = $body['message']['text'] ?? null;

        if (empty($chatId) || empty($text)) {
            return;
        }

        $text = trim($text);
        $chatId = (string) $chatId;

        if (!str_starts_with($text, self::PSIHOLOG_SETUP_PREFIX)) {
            return;
        }

        $authToken = trim(str_replace(self::PSIHOLOG_SETUP_PREFIX, '', $text));
        if (empty($authToken)) {
            return;
        }

        $autologinToken = $this->psihologAutologinTokenRepository->findByToken($authToken);
        if (!$autologinToken) {
            return;
        }

        $psiholog = $autologinToken->getPsiholog();

        $settings = $this->psihologSettingsRepository->findByPsiholog($autologinToken->getPsiholog());
        if (!$settings) {
            $settings = new PsihologSettings();
            $settings->setTimezone(TimezoneHelper::DEFAULT_TIMEZONE);
            $settings->setPsiholog($psiholog);
            $this->em->persist($settings);
        }

        $settings->setTelegramChatId($chatId);
        $this->em->flush();

        $params = [
            'chat_id' => $chatId,
            'text' => "Ура! Вы успешно активировали оповещения.",
            'parse_mode' => 'html',
        ];

        $this->telegramClient->sendMessage($params);
    }
}