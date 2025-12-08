<?php

namespace App\Psiholog\Controller\Chat;

use App\Psiholog\Service\CurrentPsihologRetriever;
use App\User\Entity\ChatMessage;
use App\User\Repository\UserPsihologRepository;
use App\User\Repository\UserRepository;
use App\Psiholog\Service\RouteNames;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/chat/send-message",
    name: "chat_send_message",
    methods: ["POST"]
)]
final class SendMessageController extends AbstractController
{
    private int $userId;

    public function __construct(
        private CurrentPsihologRetriever $currentPsihologRetriever,
        private UserRepository $userRepository,
        private EntityManagerInterface $em,
        private UserPsihologRepository $userPsihologRepository,
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $this->userId = (int) $request->request->get('userId');
        $body = trim($request->request->get('body', ''));

        $user = $this->userRepository->findById($this->userId);
        if (!$user) {
            return $this->redirectToChat();
        }
        $psiholog = $this->currentPsihologRetriever->get();

        if (!$this->userPsihologRepository->isUserBelongsToPsiholog($user->getId(), $psiholog->getId())) {
            return $this->redirectToChat();
        }

        $message = new ChatMessage();
        $message->setUser($user);
        $message->setPsiholog($psiholog);
        $message->setBody($body);
        $message->setSentByPsiholog();
        $message->setSentAt(new \DateTime());

        $this->em->persist($message);
        $this->em->flush();

        return $this->redirectToChat();
    }

    private function redirectToChat(): RedirectResponse
    {
        return $this->redirectToRoute(RouteNames::CHAT, [
            'userId' => $this->userId
        ]);
    }
}