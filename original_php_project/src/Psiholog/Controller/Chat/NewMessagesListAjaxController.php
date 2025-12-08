<?php

namespace App\Psiholog\Controller\Chat;

use App\Common\Service\ArrayIndexer;
use App\Psiholog\Repository\ChatMessageRepository;
use App\Psiholog\Service\CurrentPsihologRetriever;
use DateTime;
use DateTimeZone;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/new-messages-list-ajax",
    name: "new_messages_list_ajax",
    methods: ["GET"]
)]
final class NewMessagesListAjaxController extends AbstractController
{

    public function __construct(
        private CurrentPsihologRetriever $currentPsihologRetriever,
        private ChatMessageRepository $chatMessageRepository,
        private ArrayIndexer $arrayIndexer,
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $psiholog = $this->currentPsihologRetriever->get();

        $unreadMessages = $this->chatMessageRepository->getUnreadMessagesListGroupedByUserWithAmount($psiholog->getId());
        $unreadMessagesIndexed = $this->arrayIndexer->byKeyUnique($unreadMessages, 'id');
        $idList = array_keys($unreadMessagesIndexed);

        $messages = $this->chatMessageRepository->findByIdList($idList);

        foreach ($messages as $key => $message) {
            $message += $unreadMessagesIndexed[$message['id']];
            $messages[$key] = $message;
        }

        $content = $messages;

        return new JsonResponse($content);
    }
}