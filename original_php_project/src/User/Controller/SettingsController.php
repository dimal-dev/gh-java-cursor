<?php

namespace App\User\Controller;

use App\Common\Service\Twig\TimezoneExtension;
use App\User\Service\CurrentUserRetriever;
use App\User\Service\RouteNames;
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
        private CurrentUserRetriever $currentUserRetriever,
        private TimezoneExtension $timezoneExtension,
        private EntityManagerInterface $em,
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $content = [];

        $user = $this->currentUserRetriever->get();

        if ($request->isMethod('post')) {
            $newTimezone = $request->request->get('newTimezone');
            $timezoneList = $this->timezoneExtension->getTimezoneList();
            if (isset($timezoneList[$newTimezone]) && $newTimezone !== $user->getTimezone()) {
                $user->setTimezone($newTimezone);
            }

            $newFullName = trim(htmlspecialchars(strip_tags($request->request->get('newFullName'))));
            if ($newFullName !== $user->getFullName()) {
                $user->setFullName($newFullName);
                $user->setIsFullNameSetByUser(true);
            }
            $this->em->flush();

            return $this->redirectToRoute(RouteNames::SETTINGS);
        }

        $content['currentTimezone'] = $user->getTimezone();
        $content['email'] = $user->getEmail();
        $content['fullName'] = $user->getFullName();

        return $this->render('@user/pages/settings.html.twig', $content);
    }
}