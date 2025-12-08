<?php

namespace App\Landing\Controller;

use App\Billing\Entity\Currency;
use App\Common\Service\GaClient;
use App\Common\Service\Language;
use App\Common\Service\DateLocalizedHelper;
use App\Common\Service\TimezoneHelper;
use App\Landing\Service\CurrentTimezoneStorage;
use App\Landing\Service\PsihologProfileInfo;
use App\Landing\Service\PsihologReview;
use App\Landing\Service\RouteNames;
use App\Landing\Service\TopicList;
use App\Psiholog\Entity\Psiholog;
use App\Psiholog\Entity\PsihologPrice;
use App\Psiholog\Entity\PsihologSchedule;
use App\Psiholog\Repository\PsihologPriceRepository;
use App\Psiholog\Repository\PsihologProfileRepository;
use App\Psiholog\Repository\PsihologRepository;
use App\Psiholog\Repository\PsihologScheduleRepository;
use App\Psiholog\Repository\PsihologSettingsRepository;
use App\User\Service\TimeHelper;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpKernel\Exception\NotFoundHttpException;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;
use Symfony\Component\Security\Core\Security;

#[Route(
    "/profile/{id}",
    name: "psiholog_profile",
    requirements: ["id" => '\d+'],
    methods: ["GET", "POST"]
)]
final class PsihologProfileController extends AbstractController
{
    public const BOOK_CONSULTATION_HTML_ID = 'consultation-book-link-to';

    private Request $request;

    public function __construct(
        private PsihologPriceRepository $psihologPriceRepository,
        private PsihologProfileRepository $psihologProfileRepository,
        private PsihologRepository $psihologRepository,
        private PsihologReview $psihologReview,
        private PsihologProfileInfo $psihologProfileInfo,
        private TopicList $topicList,
        private UrlGeneratorInterface $urlGenerator,
    ) {
    }

    public function __invoke(int $id, Request $request): Response
    {
        $this->request = $request;

        $content = [];

        $psiholog = $this->psihologRepository->findById($id);
        if (!$psiholog || !$psiholog->isActive()) {
            throw new NotFoundHttpException();
        }
        $psihologProfileInfo = $this->psihologProfileInfo->get($psiholog->getId());
        if (!$psihologProfileInfo) {
            throw new NotFoundHttpException();
        }

        $psihologProfile = $this->psihologProfileRepository->findByPsiholog($psiholog);
        if (!$psihologProfile) {
            throw new NotFoundHttpException();
        }


        $slugPrice = $this->getSlugPrice($this->request);
        $content += $this->getPriceInfo($psiholog->getId(), $slugPrice);

        $content['psihologProfile'] = $psihologProfile;
        $content['psiholog'] = $psiholog;
        $content['psihologProfileInfo'] = $psihologProfileInfo;

        $content['topicsGrouped'] = $this->topicList->groupTopics($content['psihologProfileInfo']['works_with']);

        $content['locale'] = $request->getLocale();

        $content['reviews'] = $this->psihologReview->get($psiholog->getId());
        $content['slugPrice'] = $slugPrice;

        $bookConsultationLinkParams = ['id' => $psiholog->getId()];
        if ($slugPrice) {
            $bookConsultationLinkParams['tft'] = $slugPrice->getSlug();
        }
        $content['bookConsultationLink'] = $this->urlGenerator->generate(RouteNames::BOOK_CONSULTATION, $bookConsultationLinkParams);

        return $this->render('@landing/pages/psiholog-profile.html.twig', $content);
    }

    private function getPriceInfo(int $psihologId, ?PsihologPrice $slugPrice): array
    {
        $priceInfo = [];
        $prices = $this->psihologPriceRepository->findAllCurrentByIdList([$psihologId]);
        if ($slugPrice) {
            $prices[] = [
                'price' => $slugPrice->getPrice(),
            ];
        }

        if (count($prices) === 1) {
            $priceInfo['price'] = $prices[0]['price'];
            $priceInfo['price_from'] = false;
        } else {
            usort($prices, fn($p1, $p2) => $p1['price'] <=> $p2['price']);

            $priceInfo['price'] = reset($prices)['price'];
            $priceInfo['price_from'] = true;
        }

        return $priceInfo;
    }

    private function getSlugPrice(Request $request): ?PsihologPrice
    {
        if (!$request->query->has('tft')) {
            return null;
        }
        $priceSlug = $request->query->get('tft');
        if (empty($priceSlug)) {
            return null;
        }

        return $this->psihologPriceRepository->findBySlug($priceSlug);
    }
}