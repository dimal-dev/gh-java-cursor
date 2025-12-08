<?php

namespace App\Staff\Controller\Blog;

use App\Landing\Entity\PostInterface;
use App\Landing\Repository\PostRepository;
use App\Staff\Repository\PsihologRepository;
use App\Staff\Service\RouteNames;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpKernel\Exception\NotFoundHttpException;
use Symfony\Component\Routing\Annotation\Route;
use URLify;

#[Route(
    "/blog/post/edit",
    name: "blog_post_edit",
    methods: ["GET", "POST"]
)]
final class PostEditController extends AbstractController
{

    public function __construct(
        private PostRepository $postRepository,
        private PsihologRepository $psihologRepository,
        private EntityManagerInterface $em,
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $id = $request->query->get('id');
        $locale = $request->get('locale', 'ua');
        $content = [];

        $post = $this->postRepository->findByIdAndLocale($id, $locale);
        if (!$post) {
            throw new NotFoundHttpException();
        }

        if ($request->isMethod('post')) {
            $postData = $this->retrievePostData($request);
            $errors = $this->validatePostData($postData);
            if (empty($errors)) {
                $this->savePost($postData, $locale, $id, $post);

                return $this->redirectToRoute(RouteNames::BLOG_POST_EDIT, [
                    'id' => $id,
                    'locale' => $locale,
                ]);
            }
            $content['errors'] = $errors;
            $post = $postData + $post;
        }

        $content['locale'] = $locale;
        $content['post'] = $post;
        $content['psihologList'] = $this->getPsihologList();
        $content['postStateList'] = [
            PostInterface::STATE_DRAFT => 'Черновик',
            PostInterface::STATE_POSTED => 'Опубликован',
        ];

        return $this->render('@staff/pages/post/edit.html.twig', $content);
    }

    private function getPsihologList(): array
    {
        $nameWithIdList = $this->psihologRepository->findAllNameWithIdList();

        $psihologList = [];

        foreach ($nameWithIdList as ['psiholog_id' => $id, 'first_name' => $firstName, 'last_name' => $lastName]) {
            $psihologList[$id] = "$firstName $lastName ($id)";
        }

        return $psihologList;
    }

    private function retrievePostData(Request $request): array
    {
        $post = $request->request;

        $data = [];
        $data['header'] = trim(strip_tags($post->get('header', '')));
        $data['author'] = trim(strip_tags($post->get('author', '')));
        $data['psiholog_id'] = $post->get('psiholog_id', '');
        $data['state'] = $post->get('state', '');
        $data['preview'] = trim($this->replaceCopyright($post->get('preview', '')));
        $data['body'] = trim($this->replaceCopyright($post->get('body', '')));

        return $data;
    }

    private function replaceCopyright(string $original): string
    {
        $search = '<p data-f-id="pbf" style="text-align: center; font-size: 14px; margin-top: 30px; opacity: 0.65; font-family: sans-serif;">Powered by <a href="https://www.froala.com/wysiwyg-editor?pb=1" title="Froala Editor">Froala Editor</a></p>';

        return str_replace($search, '', $original);
    }

    private function validatePostData(array $postData): array
    {
        $errors = [];
        if (empty($postData['header'])) {
            $errors['header'] = 'Нужно заполнить заголовок';
        }

        if (empty(trim(strip_tags($postData['preview'])))) {
            $errors['preview'] = 'Нужно заполнить превью';
        }
        if (empty(trim(strip_tags($postData['body'])))) {
            $errors['body'] = 'Нужно заполнить содержимое статьи';
        }

        return $errors;
    }

    private function savePost(array $postData, string $locale, int $id, array $oldPostData): void
    {
        $dataToUpdate = $postData;

        if ($oldPostData['state'] == PostInterface::STATE_DRAFT && $dataToUpdate['state'] == PostInterface::STATE_POSTED) {
            $dataToUpdate['posted_at'] = date('Y-m-d H:i:s');
            $dataToUpdate['slug'] = URLify::slug($postData['header']);
        }

        if (empty($dataToUpdate['psiholog_id'])) {
            unset($dataToUpdate['psiholog_id']);
        }

        $table = "blog_post_$locale";
        $this->em->getConnection()->update($table, $dataToUpdate, [
            'id' => $id,
        ]);
    }
}