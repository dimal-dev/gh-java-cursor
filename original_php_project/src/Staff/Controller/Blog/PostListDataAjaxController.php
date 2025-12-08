<?php

namespace App\Staff\Controller\Blog;

use App\Common\Service\ArrayIndexer;
use App\Common\Service\SearchCriteriaCreator;
use App\Psiholog\Repository\PsihologProfileRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\Asset\Packages;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/blog/post/list-data-ajax",
    name: "blog_post_list_data_ajax",
    methods: ["GET"]
)]
final class PostListDataAjaxController extends AbstractController
{
    private const SORT_FIELDS = [
        'id',
    ];

    public function __construct(
        private EntityManagerInterface $em,
        private SearchCriteriaCreator $searchCriteriaCreator,
        private PsihologProfileRepository $psihologProfileRepository,
        private ArrayIndexer $arrayIndexer,
        private Packages $packages,
    ) {
    }

    public function __invoke(Request $request): JsonResponse
    {
        $content = $this->findPosts($request);

        return new JsonResponse($content);
    }

    private function findPosts(Request $request): array
    {
        $criteria = $this->searchCriteriaCreator->create($request->query->all());
        $locale = $request->query->get('locale', 'ua');

        $tableName = "blog_post_{$locale}";

        $qb = $this->em->getConnection()->createQueryBuilder();

        $qb->select([
            't.id',
            't.author',
            't.psiholog_id',
            't.header',
            't.preview',
            't.state',
            't.slug',
            't.posted_at',
            't.post_i18n_id',
            't.date_created',
        ]);
        $qb->from($tableName, 't');
        $qb->orderBy('id', 'DESC');
        $qb->setFirstResult($criteria->offset);
        $qb->setMaxResults($criteria->limit);

        $posts = $qb->execute()->fetchAllAssociative();

        $psihologIdList = array_column($posts, 'psiholog_id', 'psiholog_id');

        if (!empty($psihologIdList)) {
            $profileList = $this->psihologProfileRepository->findAllByIdList($psihologIdList);
            $profileListIndexed = $this->arrayIndexer->byKeyUnique($profileList, 'psiholog_id');

            foreach ($posts as $key => $post) {
                if (!empty($post['psiholog_id'])) {
                    $profile = $profileListIndexed[$post['psiholog_id']];

                    $post['psihologFullName'] = "{$profile['first_name']} {$profile['last_name']}";
                    $post['psihologAvatar'] = $this->packages->getUrl("media/psiholog-profile/{$profile['profile_template']}.jpg", 'user');

                    $posts[$key] = $post;
                }
            }
        }


        return $posts;
    }
}