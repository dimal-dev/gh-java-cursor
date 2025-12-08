<?php

namespace App\Psiholog\Controller;

use App\Common\Service\TimezoneHelper;
use App\Notification\Controller\Telegram\WebhookController;
use App\Psiholog\Entity\PsihologSettings;
use App\Psiholog\Repository\PsihologAutologinTokenRepository;
use App\Psiholog\Repository\PsihologSettingsRepository;
use App\Psiholog\Service\CurrentPsihologRetriever;
use App\Psiholog\Service\RouteNames;
use DateTime;
use DateTimeZone;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/settings",
    name: "settings",
    methods: ["GET", "POST"]
)]
final class SettingsController extends AbstractController
{

    public function __construct(
        private PsihologSettingsRepository $psihologSettingsRepository,
        private CurrentPsihologRetriever $currentPsihologRetriever,
        private EntityManagerInterface $em,
        private PsihologAutologinTokenRepository $psihologAutologinTokenRepository,
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $content = [];

        $psiholog = $this->currentPsihologRetriever->get();
        $settings = $this->psihologSettingsRepository->findByPsiholog($psiholog);
        if (!$settings) {
            $settings = new PsihologSettings();
            $settings->setPsiholog($psiholog);
            $settings->setTimezone("Europe/Kiev");

            $this->em->persist($settings);
            $this->em->flush();
        }

        if ($request->isMethod('post')) {
            $newTimezone = (string) $request->request->get('newTimezone');
            if ($settings->getTimezone() !== $newTimezone) {
                $settings->setTimezone($newTimezone);
                $this->em->flush();

                return $this->redirectToRoute(RouteNames::SETTINGS);
            }
        }

        $token = $psiholog->getPsihologAutologinToken();
        $content['telegramLinkToken'] = WebhookController::PSIHOLOG_SETUP_PREFIX . $token->getToken();

        $content['psihologTimezone'] = $settings->getTimezone();

        return $this->render('@psiholog/pages/settings.html.twig', $content);
    }
}