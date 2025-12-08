<?php

namespace App\Staff\Controller\Blog;

use App\Blog\Entity\Post;
use App\Landing\Entity\PostInterface;
use App\Staff\Repository\PsihologRepository;
use App\Staff\Service\RouteNames;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use URLify;

#[Route(
    "/blog/post/add",
    name: "blog_post_add",
    methods: ["GET", "POST"]
)]
final class PostAddController extends AbstractController
{

    public function __construct(
        private PsihologRepository $psihologRepository,
        private EntityManagerInterface $em
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $content = [];
        $locale = $request->get('locale', 'ua');

        try {
            if ($request->isMethod(Request::METHOD_POST)) {
                $postData = $this->retrievePostData($request);
                $errors = $this->validatePostData($postData);
                if (empty($errors)) {
                    $postId = $this->addNewPost($postData, $locale);

                    return $this->redirectToRoute(RouteNames::BLOG_POST_EDIT, [
                        'id' => $postId,
                        'locale' => $locale,
                    ]);
                }
                $content['postSubmittedData'] = $postData;
                $content['errors'] = $errors;
            } else {
                if ($request->query->has('psiholog_id')) {
                    $content['postSubmittedData']['psiholog_id'] = $request->query->get('psiholog_id');
                }
                if ($request->query->has('author')) {
                    $content['postSubmittedData']['author'] = $request->query->get('author');
                }
            }
            $content['psihologList'] = $this->getPsihologList();
            $content['locale'] = $locale;
        } catch (\Exception $e) {
            echo '<pre>';
            echo $e;
            die;
        }

        $content['post_i18n_id'] = $request->query->get('post_i18n_id');

        return $this->render('@staff/pages/post/add.html.twig', $content);
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

    private function addNewPost(array $postData, string $locale): int
    {
        $dataToInsert = $postData;
        if (empty($dataToInsert['psiholog_id'])) {
            unset($dataToInsert['psiholog_id']);
        }
        $dataToInsert['state'] = PostInterface::STATE_DRAFT;
        $dataToInsert['slug'] = URLify::slug($postData['header']);
        $dataToInsert['posted_at'] = date('Y-m-d H:i:s');

        $table = "blog_post_$locale";
        $this->em->getConnection()->insert($table, $dataToInsert);

        $postId = $this->em->getConnection()->lastInsertId();

        if (!empty($dataToInsert['post_i18n_id'])) {
            $localeReversed = $locale === 'ru' ? 'ua' : 'ru';
            $this->em->getConnection()->update("blog_post_$localeReversed", [
                'post_i18n_id' => $postId,
            ], [
                'id' => $dataToInsert['post_i18n_id']
            ]);
        }

        return $postId;
    }

    private function retrievePostData(Request $request): array
    {
        $post = $request->request;

        $data = [];
        $data['header'] = trim(strip_tags($post->get('header', '')));
        $data['author'] = trim(strip_tags($post->get('author', '')));
        $data['psiholog_id'] = $post->get('psiholog_id', '');
        $data['preview'] = trim($this->replaceCopyright($post->get('preview', '')));
        $data['body'] = trim($this->replaceCopyright($post->get('body', '')));
        $data['post_i18n_id'] = (int) $post->get('post_i18n_id');

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
}