<?php

namespace App\User\Controller\Chat;

use App\Psiholog\Repository\PsihologRepository;
use App\User\Entity\ChatMessage;
use App\User\Repository\UserPsihologRepository;
use App\User\Service\CurrentUserRetriever;
use App\User\Service\RouteNames;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;

#[Route(
    "/chat/send-message",
    name: "chat_send_message",
    methods: ["POST"]
)]
final class SendMessageController extends AbstractController
{

    public function __construct(
        private CurrentUserRetriever $currentUserRetriever,
        private EntityManagerInterface $em,
        private UserPsihologRepository $userPsihologRepository,
        private PsihologRepository $psihologRepository,
        private UrlGeneratorInterface $urlGenerator,
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $body = trim($request->request->get('body', ''));
        if (!empty($body)) {
            $user = $this->currentUserRetriever->get();
            $psihologId = (int) $request->request->get('pid');
            if ($this->userPsihologRepository->isUserBelongsToPsiholog($user->getId(), $psihologId)) {
                $psiholog = $this->psihologRepository->findById($psihologId);

                $message = new ChatMessage();
                $message->setUser($user);
                $message->setPsiholog($psiholog);
                $message->setBody($body);
                $message->setSentByUser();
                $message->setSentAt(new \DateTime());

                $this->em->persist($message);
                $this->em->flush();
            }
        }

        return $this->redirectBack($request);
    }

    private function redirectBack(Request $request): RedirectResponse
    {
        $url = $request->server->get('HTTP_REFERER', $this->urlGenerator->generate(RouteNames::CHAT));

        return $this->redirect($url);
    }
}