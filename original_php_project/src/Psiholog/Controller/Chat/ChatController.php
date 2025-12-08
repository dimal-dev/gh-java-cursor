<?php

namespace App\Psiholog\Controller\Chat;

use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/chat",
    name: "chat",
    methods: ["GET"]
)]
final class ChatController extends AbstractChatController
{
    public function __invoke(Request $request): Response
    {
        $content = $this->setupContent($request);

        return $this->render('@psiholog/pages/chat/chat.html.twig', $content);
    }
}