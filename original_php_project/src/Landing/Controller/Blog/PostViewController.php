<?php

namespace App\Landing\Controller\Blog;

use App\Landing\Repository\PostRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpKernel\Exception\NotFoundHttpException;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Core\Security;

#[Route(
    "/blog/post-view/{postId}/{slug}",
    name: "blog_post_view",
    methods: ["GET"]
)]
final class PostViewController extends AbstractController
{
    public function __construct(
        private PostRepository $postRepository,
    ) {
    }

    public function __invoke(Request $request, int $postId, string $slug): Response
    {
        $content = [];

        $locale = $request->getLocale();

        $showDraft = $this->isGranted('ROLE_STAFF_USER');

        $post = $this->postRepository->findByIdAndLocaleAndSlugPosted($postId, $locale, $slug, $showDraft);
        if (!$post) {
            throw new NotFoundHttpException();
        }

        $content['post'] = $post;
        if (!empty($post['post_i18n_id'])) {
            $localeReversed = $locale === 'ua' ? 'ru' : 'ua';
            $content['post_i18n_short'] = $this->postRepository->findByIdAndLocalePostedShort(
                $post['post_i18n_id'],
                $localeReversed,
                $showDraft
            );
        }

        $content['locale'] = $locale;

        return $this->render('@landing/pages/blog/post-view.html.twig', $content);
    }
}