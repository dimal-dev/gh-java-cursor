<?php

namespace App\Psiholog\Controller\Chat;

use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/chat/get-messages-ajax",
    name: "chat_get_messages_ajax",
    methods: ["GET"]
)]
final class GetMessagesAjaxController extends AbstractChatController
{
    public function __invoke(Request $request): Response
    {
        $content = $this->setupContent($request);

        $responseContent = [];
        $responseContent['messagesHtml'] = '';
        $responseContent['latestChatMessageId'] = 0;

        if (!empty($content['chatMessages'])) {
            $responseContent['messagesHtml'] = $this->renderView('@psiholog/pages/chat/chat_messages.html.twig', $content);
            $responseContent['latestChatMessageId'] = end($content['chatMessages'])['id'];
        }

        return new JsonResponse($responseContent);
    }
}