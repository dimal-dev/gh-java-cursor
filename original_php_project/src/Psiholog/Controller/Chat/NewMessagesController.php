<?php

namespace App\Psiholog\Controller\Chat;

use DateTime;
use DateTimeZone;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/new-messages",
    name: "new_messages",
    methods: ["GET"]
)]
final class NewMessagesController extends AbstractController
{

    public function __construct(
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $content = [];

        return $this->render('@psiholog/pages/chat/new-messages.html.twig', $content);
    }
}