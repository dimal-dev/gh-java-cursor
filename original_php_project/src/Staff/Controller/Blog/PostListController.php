<?php

namespace App\Staff\Controller\Blog;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/blog/post/list",
    name: "blog_post_list",
    methods: ["GET"]
)]
final class PostListController extends AbstractController
{

    public function __construct(
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $content = [];
        $content['locale'] = $request->query->get('locale', 'ua');

        return $this->render('@staff/pages/post/list.html.twig', $content);
    }
}