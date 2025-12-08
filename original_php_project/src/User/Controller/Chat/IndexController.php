<?php

namespace App\User\Controller\Chat;

use App\Common\Service\ArrayIndexer;
use App\Psiholog\Repository\PsihologProfileRepository;
use App\User\Entity\User;
use App\User\Service\ChatMessagesRetriever;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Core\Security;

#[Route(
    "/chat",
    name: "chat",
    methods: ["GET"]
)]
final class IndexController extends AbstractController
{
    public function __construct(
        private Security $security,
        private EntityManagerInterface $em,
        private ChatMessagesRetriever $chatMessagesRetriever,
    ) {
    }

    public function __invoke(Request $request): Response
    {
        /** @var User $user */
        $user = $this->security->getUser()->getUser();

        $psihologList = $this->getPsihologList($user);

        $selectedPsihologId = (int) $request->query->get('pid');
        if (empty($selectedPsihologId)) {
            $firstPsiholog = reset($psihologList);
            if (!empty($firstPsiholog)) {
                $selectedPsihologId = $firstPsiholog['psiholog_id'];
            }
        } else {
            if (!isset(array_column($psihologList, 'psiholog_id', 'psiholog_id')[$selectedPsihologId])) {
                $firstPsiholog = reset($psihologList);
                if (!empty($firstPsiholog)) {
                    $selectedPsihologId = $firstPsiholog['psiholog_id'];
                }
            }
        }

        $content = [];
        $content['latestChatMessageId'] = 0;

        if (!empty($selectedPsihologId)) {
            $chatMessages = $this->chatMessagesRetriever->get($user, $selectedPsihologId);
            $content['chatMessages'] = $chatMessages;
            if (!empty($chatMessages)) {
                $content['latestChatMessageId'] = end($chatMessages)['id'];
            }
        }
        $content['psihologList'] = $psihologList;
        $content['selectedPsihologId'] = $selectedPsihologId;

        $request->getSession()->remove(ChatMessagesRetriever::UNREAD_MESSAGES_SESSION_KEY);

        return $this->render('@user/pages/chat/index.html.twig', $content);
    }

    private function getPsihologList(User $user): array
    {
        $qb = $this->em->getConnection()->createQueryBuilder();
        $qb->select(
            [
                'user_id',
                'psiholog_id',
            ]
        );
        $qb->from('user_psiholog', 'up');

        $qb->andWhere('up.user_id = :userId');
        $qb->setParameter('userId', $user->getId());

        return $qb->execute()->fetchAllAssociative();
    }
}