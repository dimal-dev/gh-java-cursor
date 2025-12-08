<?php

namespace App\User\Controller\Chat;

use App\User\Repository\ChatMessageRepository;
use App\User\Service\CurrentUserRetriever;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/chat/get-unread-messages-amount-ajax",
    name: "chat_get_unread_messages_amount_ajax",
    methods: ["GET"]
)]
final class GetUnreadMessagesAmountAjaxController extends AbstractController
{
    public function __construct(
        private ChatMessageRepository $chatMessageRepository,
        private CurrentUserRetriever $currentUserRetriever
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $user = $this->currentUserRetriever->get();
        $psiholog = $user->getUserPsiholog()->getPsiholog();

        $content = [];
        $content['amount'] = $this->chatMessageRepository->getUnreadMessageFromPsihologAmount(
            $user->getId(),
            $psiholog->getId()
        );

        return new JsonResponse($content);
    }
}