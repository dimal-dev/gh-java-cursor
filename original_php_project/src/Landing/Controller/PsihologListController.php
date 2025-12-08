<?php

namespace App\Landing\Controller;

use App\Common\Service\ArrayIndexer;
use App\Common\Service\Pluralizer;
use App\Landing\Service\PsihologList;
use App\Landing\Service\PsihologProfileInfo;
use App\Landing\Service\PsihologReview;
use App\Landing\Service\RouteNames;
use App\Psiholog\Entity\Psiholog;
use App\Psiholog\Repository\PsihologPriceRepository;
use App\Psiholog\Repository\PsihologProfileRepository;
use Psr\Log\LoggerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\Asset\Package;
use Symfony\Component\Asset\Packages;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/psiholog-list",
    name: "psiholog_list",
    methods: ["GET"]
)]
final class PsihologListController extends AbstractController
{
    public function __construct(
        private PsihologList $psihologList,
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $content = [];

        $content['psihologList'] = $this->psihologList->getPublicPsihologList();

        return $this->render('@landing/pages/psiholog-list.html.twig', $content);
    }
}