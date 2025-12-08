<?php

namespace App\Psiholog\Controller;

use App\Common\Service\TimezoneHelper;
use App\Notification\Controller\Telegram\WebhookController;
use App\Psiholog\Entity\PsihologSettings;
use App\Psiholog\Entity\PsihologUserNotes;
use App\Psiholog\Repository\PsihologAutologinTokenRepository;
use App\Psiholog\Repository\PsihologSettingsRepository;
use App\Psiholog\Repository\PsihologUserNotesRepository;
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
    "/user-notes",
    name: "user_notes",
    methods: ["GET", "POST"]
)]
final class UserNotesController extends AbstractController
{
    private const RETURN_TO_URL_KEY = 'rtu_un';

    public function __construct(
        private CurrentPsihologRetriever $currentPsihologRetriever,
        private EntityManagerInterface $em,
        private PsihologUserNotesRepository $psihologUserNotesRepository,
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $session = $request->getSession();

        $referer = $request->server->get('HTTP_REFERER');
        $thisUri = $request->server->get('REQUEST_URI');
        if (!empty($referer)) {
            $refererPath = parse_url($referer, PHP_URL_PATH);
            $thisPath = parse_url($thisUri, PHP_URL_PATH);
            if ($refererPath !== $thisPath) {
                $session->set(self::RETURN_TO_URL_KEY, $referer);
            }
        }

        $content = [];

        $psiholog = $this->currentPsihologRetriever->get();
        $userId = (int) $request->query->get('userId', 0);
        $userNotes = $this->psihologUserNotesRepository->findByPsihologAndUserId($psiholog, $userId);

        if ($request->isMethod('post')) {
            if (!$userNotes) {
                $userNotes = new PsihologUserNotes();
                $userNotes->setPsiholog($psiholog);
                $userNotes->setUserId($userId);
                $this->em->persist($userNotes);
            }

            $name = trim((string) $request->request->get('newName'));
            $userNotes->setName($name);
            $this->em->flush();

            if ($session->has(self::RETURN_TO_URL_KEY)) {
                $redirectUrl = $session->get(self::RETURN_TO_URL_KEY);
                $session->remove(self::RETURN_TO_URL_KEY);

                return $this->redirect($redirectUrl);
            }

            return $this->redirect($request->getUri());
        }

        $content['userName'] = $userNotes ? $userNotes->getName() : '';

        return $this->render('@psiholog/pages/user-notes.html.twig', $content);
    }
}