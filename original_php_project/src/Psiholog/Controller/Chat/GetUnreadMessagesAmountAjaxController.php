<?php

namespace App\Psiholog\Controller\Chat;

use App\Psiholog\Repository\ChatMessageRepository;
use App\Psiholog\Service\CurrentPsihologRetriever;
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
        private CurrentPsihologRetriever $currentPsihologRetriever,
        private ChatMessageRepository $chatMessageRepository,
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $psiholog = $this->currentPsihologRetriever->get();

        $content = [];
        $content['amount'] = $this->chatMessageRepository->getUnreadMessageFromUsersAmount($psiholog->getId());

        return new JsonResponse($content);
    }
}