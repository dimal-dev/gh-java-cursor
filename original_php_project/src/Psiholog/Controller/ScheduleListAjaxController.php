<?php

namespace App\Psiholog\Controller;

use App\Common\Service\DateLocalizedHelper;
use App\Common\Service\SearchCriteria;
use App\Common\Service\SearchCriteriaCreator;
use App\Psiholog\Entity\PsihologSchedule;
use App\Psiholog\Service\CurrentPsihologRetriever;
use App\Psiholog\Service\TimeHelper;
use App\User\Entity\UserConsultation;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/schedule-list-ajax",
    name: "schedule_list_ajax",
    methods: ["GET"]
)]
final class ScheduleListAjaxController extends AbstractController
{

    public function __construct(
        private EntityManagerInterface $em,
        private CurrentPsihologRetriever $currentPsihologRetriever,
        private TimeHelper $timeHelper,
        private DateLocalizedHelper $dateLocalizedHelper,
        private SearchCriteriaCreator $searchCriteriaCreator,
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $criteria = $this->searchCriteriaCreator->create($request->query->all());
        $content = $this->findScheduleList($criteria);

        foreach ($content as $key => $row) {
            $row['available_at_timestamp_utc'] = strtotime($row['available_at']);
            $availableAtTzAware = $this->timeHelper->toPsihologTz($row['available_at']);
            $row['available_at'] = $availableAtTzAware;
            $row['available_at_label'] = $this->dateLocalizedHelper->getDateTimeGoodLookingLabel(new \DateTime($availableAtTzAware));
            $content[$key] = $row;
        }

        return new JsonResponse($content);
    }

    private function findScheduleList(SearchCriteria $searchCriteria): array
    {
        $psiholog = $this->currentPsihologRetriever->get();

        $qb = $this->em->getConnection()->createQueryBuilder();

        $qb->select(
            'uc.id',
            'uc.type',
            'IFNULL(u.full_name, "клиент не указал") as full_name',
            'u.is_full_name_set_by_user',
            'u.id as user_id',
            'MIN(ps.available_at) as available_at',
            'MIN(ps.id) as ps_id',
            'IFNULL(pun.name, "") as psiholog_user_notes_name',
        );
        $qb->from('psiholog_schedule', 'ps');
        $qb->innerJoin('ps', 'user_consultation_psiholog_schedule', 'ucps', 'ps.id = ucps.psiholog_schedule_id');
        $qb->innerJoin('ucps', 'user_consultation', 'uc', 'ucps.user_consultation_id = uc.id');

        $qb->innerJoin('uc', 'user', 'u', 'uc.user_id = u.id');

        $qb->leftJoin('uc', 'psiholog_user_notes', 'pun', 'ps.psiholog_id = pun.psiholog_id and uc.user_id = pun.user_id');

        $qb->andWhere('ps.state = :psState');
        $qb->setParameter('psState', PsihologSchedule::STATE_BOOKED);

        $qb->andWhere('uc.state = :ucState');
        $qb->setParameter('ucState', UserConsultation::STATE_CREATED);

        $qb->andWhere('ps.psiholog_id = :psihologId');
        $qb->setParameter('psihologId', $psiholog->getId());

        $qb->andWhere('ps.available_at > :from');
        $qb->setParameter('from', date('Y-m-d H:i:s'));

        $qb->groupBy('uc.id');
        $qb->orderBy('available_at', 'ASC');

        $qb->setFirstResult($searchCriteria->offset);
        $qb->setMaxResults($searchCriteria->limit);

        return $qb->execute()->fetchAllAssociative();
    }
}