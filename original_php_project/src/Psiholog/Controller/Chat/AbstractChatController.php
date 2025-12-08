<?php

namespace App\Psiholog\Controller\Chat;

use App\Psiholog\Repository\PsihologUserNotesRepository;
use App\Psiholog\Service\ChatMessagesRetriever;
use App\Psiholog\Service\CurrentPsihologRetriever;
use App\User\Repository\UserRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

abstract class AbstractChatController extends AbstractController
{
    public function __construct(
        private CurrentPsihologRetriever $currentPsihologRetriever,
        private UserRepository $userRepository,
        private PsihologUserNotesRepository $psihologUserNotesRepository,
        private ChatMessagesRetriever $chatMessagesRetriever,
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $content = $this->setupContent($request);

        return $this->render('@psiholog/pages/chat/chat.html.twig', $content);
    }

    protected function setupContent(Request $request): array
    {
        $psiholog = $this->currentPsihologRetriever->get();
        $userId = (int) $request->query->get('userId', 0);

        $user = $this->userRepository->findById($userId);
        $userNotes = $this->psihologUserNotesRepository->findByPsihologAndUserId($psiholog, $userId);

        $userName = 'Неопределен';
        if ($user) {
            $userName = '';
            $userName .= $user->getFullName();
            if ($userNotes && !empty($userNotes->getName())) {
                if (!empty($userName)) {
                    $userName .= ' / ';
                }
                $userName .= $userNotes->getName();
            }
            if (!$user->isFullNameSetByUser() && !empty($user->getFullName())) {
                $userName .= ' / Имя может быть неверным, узнайте у пользователя. <br>';
            }
        }

        $latestChatMessageId = (int) $request->query->get('latestChatMessageId', 0);
        $chatMessages = $this->chatMessagesRetriever->get($user->getId(), $psiholog, $latestChatMessageId);

        $content['chatMessages'] = $chatMessages;
        $content['userName'] = $userName;
        $content['latestChatMessageId'] = 0;
        if (!empty($chatMessages)) {
            $content['latestChatMessageId'] = end($chatMessages)['id'];
        }

        return $content;
    }
}