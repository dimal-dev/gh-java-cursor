<?php

namespace App\User\Controller\Chat;

use App\User\Entity\ChatMessage;
use App\User\Repository\ChatMessageRepository;
use App\User\Service\ChatMessagesRetriever;
use App\User\Service\CurrentUserRetriever;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpFoundation\Session\Storage\SessionStorageInterface;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/chat/get-messages-ajax",
    name: "chat_get_messages_ajax",
    methods: ["GET"]
)]
final class GetMessagesAjaxController extends AbstractController
{
    public function __construct(
        private CurrentUserRetriever $currentUserRetriever,
        private ChatMessagesRetriever $chatMessagesRetriever,
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $content = [];

        $user = $this->currentUserRetriever->get();
        $psihologId = (int) $request->query->get('pid', null);
        $latestChatMessageId = (int) $request->query->get('latestChatMessageId', 0);

        $content['chatMessages'] = $this->chatMessagesRetriever->get(
            $user,
            $psihologId,
            $latestChatMessageId
        );

        $responseContent = [];
        $responseContent['messagesHtml'] = '';
        $responseContent['latestChatMessageId'] = 0;

        if (!empty($content['chatMessages'])) {
            $responseContent['messagesHtml'] = $this->renderView('@user/pages/chat/chat_messages.html.twig', $content);
            $responseContent['latestChatMessageId'] = end($content['chatMessages'])['id'];
        }

        return new JsonResponse($responseContent);
    }
}