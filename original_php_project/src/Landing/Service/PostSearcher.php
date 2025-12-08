<?php

namespace App\Landing\Service;

use App\Common\Service\ArrayIndexer;
use App\Common\Service\SearchCriteria;
use App\Landing\Repository\PostRepository;
use App\Psiholog\Repository\PsihologProfileRepository;
use Symfony\Component\Asset\Packages;

class PostSearcher
{
    public const LIMIT_ADDITION = 2;

    public function __construct(
        private PostRepository $postRepository,
        private PsihologProfileRepository $psihologProfileRepository,
        private ArrayIndexer $arrayIndexer,
        private Packages $packages,
    ) {
    }

    public function search(SearchCriteria $searchCriteria, string $locale): PostSearcherResult
    {
        $limit = $searchCriteria->limit + self::LIMIT_ADDITION;
        $postsList = $this->postRepository->findPostList($locale, $limit, $searchCriteria->offset);

        $postsListCount = count($postsList);
        $morePostsExist = $this->isMorePostsExist($postsListCount, $searchCriteria);

        $resultingPostsList = $morePostsExist ? array_slice($postsList, 0, $searchCriteria->limit) : $postsList;

        $psihologIdList = array_column($resultingPostsList, 'psiholog_id', 'psiholog_id');

        if (!empty($psihologIdList)) {
            $profileList = $this->psihologProfileRepository->findAllByIdList($psihologIdList);
            $profileListIndexed = $this->arrayIndexer->byKeyUnique($profileList, 'psiholog_id');

            foreach ($resultingPostsList as $key => $post) {
                if (!empty($post['psiholog_id'])) {
                    $profile = $profileListIndexed[$post['psiholog_id']];

                    $post['psihologFullName'] = "{$profile['first_name']} {$profile['last_name']}";
                    $post['psihologAvatar'] = $this->packages->getUrl("media/psiholog-profile/{$profile['profile_template']}.jpg", 'user');

                    $resultingPostsList[$key] = $post;
                }
            }
        }

        return new PostSearcherResult($resultingPostsList, $morePostsExist);
    }

    private function isMorePostsExist($postsListCount, SearchCriteria $searchCriteria): bool
    {
        if ($postsListCount < 1) {
            return false;
        }

        $delta = $postsListCount - $searchCriteria->limit;

        return $delta === self::LIMIT_ADDITION;
    }
}
