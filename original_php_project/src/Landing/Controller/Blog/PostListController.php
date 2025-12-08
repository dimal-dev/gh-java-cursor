<?php

namespace App\Landing\Controller\Blog;

use App\Common\Service\SearchCriteriaCreator;
use App\Landing\Service\LocaleManager;
use App\Landing\Service\PostSearcher;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/blog/post-list",
    name: "blog_post_list",
    methods: ["GET"]
)]
final class PostListController extends AbstractController
{
    public function __construct(
        private SearchCriteriaCreator $searchCriteriaCreator,
        private PostSearcher $postSearcher,
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $locale = $request->getLocale();
        $paginationConfig = [];
        $paginationConfig['pagination']['perpage'] = 6;
        $paginationConfig['pagination']['page'] = (int) $request->query->get('page', 1);
        $criteria = $this->searchCriteriaCreator->create($paginationConfig);
        $postsSearcherResult = $this->postSearcher->search($criteria, $locale);

        $content = [
            'postsList' => $postsSearcherResult->getPostList(),
            'isMorePostsExist' => $postsSearcherResult->isMorePostsExist(),
            'page' => $criteria->page,
        ];

        return $this->render('@landing/pages/blog/post-list.html.twig', $content);
    }
}