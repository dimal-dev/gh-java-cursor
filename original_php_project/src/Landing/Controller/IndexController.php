<?php

namespace App\Landing\Controller;

use App\Common\Service\ArrayIndexer;
use App\Common\Service\Pluralizer;
use App\Landing\Service\PsihologReview;
use App\Psiholog\Entity\Psiholog;
use App\Psiholog\Repository\PsihologPriceRepository;
use App\Psiholog\Repository\PsihologProfileRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\Asset\Packages;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Core\Security;

#[Route(
    "/",
    name: "main_page",
    methods: ["GET", "POST"]
)]
final class IndexController extends AbstractController
{

    public function __construct()
    {
    }

    public function __invoke(Request $request): Response
    {
        $content = [];

        $faq = [
            [
                'title' => 'faq_main_title_1',
                'body' => 'faq_main_body_1',
            ],
            [
                'title' => 'faq_main_title_2',
                'body' => 'faq_main_body_2',
            ],
            [
                'title' => 'faq_main_title_3',
                'body' => 'faq_main_body_3',
            ],
            [
                'title' => 'faq_main_title_4',
                'body' => 'faq_main_body_4',
            ],
            [
                'title' => 'faq_main_title_5',
                'body' => 'faq_main_body_5',
            ],
            [
                'title' => 'faq_main_title_6',
                'body' => 'faq_main_body_6',
            ],
            [
                'title' => 'faq_main_title_7',
                'body' => 'faq_main_body_7',
            ],
        ];

        $content['faq_column_1'] = array_splice($faq, 0, (int) (ceil(count($faq) * 0.5)));
        $content['faq_column_2'] = $faq;

        $content['headerWithoutShadow'] = true;

        return $this->render('@landing/pages/index.html.twig', $content);
    }
}