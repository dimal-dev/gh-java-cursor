<?php

namespace App\Landing\Controller;

use App\Billing\Entity\Currency;
use App\Landing\Repository\UserRequestPsihologRepository;
use App\Landing\Service\PsihologProfileInfo;
use App\Landing\Service\RouteNames;
use App\Landing\Service\TopicList;
use App\Psiholog\Entity\Psiholog;
use App\Psiholog\Entity\PsihologPrice;
use App\Psiholog\Repository\PsihologPriceRepository;
use App\Psiholog\Repository\PsihologProfileRepository;
use App\Psiholog\Service\TelegramNotifier;
use App\User\Entity\UserRequestPsiholog;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Component\HttpFoundation\Cookie;
use Hashids\Hashids;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use function Sentry\captureException;

#[Route(
    "/request-psiholog",
    name: "request_psiholog",
    methods: ["GET", "POST"]
)]
final class RequestPsihologController extends AbstractController
{
    public const USER_REQUEST_PSIHOLOG_COOKIE_NAME = 'g63as_';

    public function __construct(
        private EntityManagerInterface $em,
        private TelegramNotifier $telegramNotifier,
        private TopicList $topicList,
        private PsihologProfileRepository $psihologProfileRepository,
        private PsihologProfileInfo $psihologProfileInfo,
        private PsihologPriceRepository $psihologPriceRepository,
        private Hashids $hashids,
        private UserRequestPsihologRepository $userRequestPsihologRepository
    ) {
    }

    public function __invoke(Request $request): Response
    {
        if ($request->isMethod('post')) {
            if ($userRequestPsiholog = $this->saveRequest($request)) {
                $params = [];
                if ($request->query->has('id')) {
                    $params['id'] = $request->query->get('id');
                }

                $response = $this->redirectToRoute(RouteNames::REQUEST_PSIHOLOG_SUCCESS, $params);

                $cookie = Cookie::create(self::USER_REQUEST_PSIHOLOG_COOKIE_NAME)
                    ->withValue($this->hashids->encode($userRequestPsiholog->getId()))
                    ->withExpires(strtotime('+10 year'))
                    ->withDomain($request->getHost())
                    ->withSecure(true);
                $response->headers->setCookie($cookie);

                return $response;
            }
            $content['error'] = true;
        }

        $content = [];

        $content['priceConfig'] = [
            'individual' => [
                800, 1000, 1500,
            ],
            'couple' => [
                1500, 2250, 3000,
            ],
            'teenager' => [
                1000, 1500, 2000,
            ],
        ];

        if ($request->query->has('id')) {
            $psihologId = (int) $request->query->get('id');
            $psihologProfile = $this->psihologProfileRepository->findByPsihologId($psihologId);
            if ($psihologProfile) {
                $content['psihologProfile'] = $psihologProfile;
                $content['psihologProfileInfo'] = $this->psihologProfileInfo->get($psihologId);

                $prices = $this->getPrices($request, $psihologProfile->getPsiholog());
                $pricesPlain = [];

                $minimalPrice = 0;
                $priceCount = count($prices);
                if (isset($prices['priceIndividual'])) {
                    $pricesPlain['For_myself']['price'] = $prices['priceIndividual'];
                    $minimalPrice = $prices['priceIndividual']->getPrice();
                }

                if (isset($prices['priceCouple'])) {
                    $pricesPlain['For_couple']['price'] = $prices['priceCouple'];
                    if ($priceCount === 1) {
                        $minimalPrice = $prices['priceCouple']->getPrice();
                    }
                }

                $content['prices'] = $prices;
                $content['pricesPlain'] = $pricesPlain;
                $content['minimalPrice'] = $minimalPrice;
                $content['isPriceFrom'] = $priceCount > 1;
            }
            $content['topicsGrouped'] = $this->topicList->groupTopics($content['psihologProfileInfo']['works_with']);
        } else {
            $content['topicsGrouped'] = $this->topicList->groupTopics(TopicList::ALL_TOPICS);
        }

        $userRequestPsihologIdEncoded = $request->cookies->get(self::USER_REQUEST_PSIHOLOG_COOKIE_NAME);
        if ($userRequestPsihologIdEncoded) {
            $userRequestPsihologId = $this->hashids->decode($userRequestPsihologIdEncoded);
            if ($userRequestPsihologId) {
                $userRequestPsihologId = reset($userRequestPsihologId);
                $userRequestPsiholog = $this->userRequestPsihologRepository->findById($userRequestPsihologId);
                if ($userRequestPsiholog) {
                    if (!empty($psihologProfile) && $psihologProfile->getPsihologId() === $userRequestPsiholog->getPsihologId()) {
                        $newUserRequestPsiholog = $this->duplicateUserRequestPsiholog($userRequestPsiholog);
                        $params['id'] = $newUserRequestPsiholog->getPsihologId();
                        $params['subsequent'] = true;

                        return $this->redirectToRoute(RouteNames::REQUEST_PSIHOLOG_SUCCESS, $params);
                    }
                }
            }
        }

        return $this->render('@landing/pages/request-psiholog.html.twig', $content);
    }

    private function prepareFormData(array $formData): ?array
    {
        $fields = [
            'name',
            'email',
            'phone',
            'sex',
            'type',
            'price',
            'channel',
            'lgbtq',
        ];

        $resultData = $formData;

        foreach ($fields as $field) {
            $value = $this->getField($field, $formData);
            if (!isset($value) || strlen($value) < 1) {
                return null;
            }

            $resultData[$field] = $value;
        }

        return $resultData;
    }

    private function getField(string $name, array $formData): string
    {
        return trim(strip_tags($formData[$name] ?? ''));
    }

    private function duplicateUserRequestPsiholog(UserRequestPsiholog $userRequestPsiholog): ?UserRequestPsiholog
    {
        try {
            $newUserRequestPsiholog = clone $userRequestPsiholog;
            $this->em->persist($newUserRequestPsiholog);
            $this->em->flush();

            $this->telegramNotifier->notifyAdminsPsihologRequested($newUserRequestPsiholog, true);

            return $newUserRequestPsiholog;
        } catch (\Exception $e) {
            captureException($e);

            return null;
        }
    }

    private function saveRequest(Request $request): ?UserRequestPsiholog
    {
        $formData = $request->request->all();
        if ($request->query->has('id')) {
            $priceId = (int) $request->request->get('price');
            $price = $this->psihologPriceRepository->findById($priceId);
            if (!$price) {
                return null;
            }

            if ($price->isIndividual()) {
                $formData['type'] = 'individual';
            } else {
                $formData['type'] = 'couple';
            }
            $formData['psihologId'] = (int)$request->query->get('id');
            $formData['sex'] = 'both';
            $formData['lgbtq'] = 0;
            $formData['price'] = $price->getPrice();
        }

        $formData = $this->prepareFormData($formData);
        $formData['promocode'] = trim(strip_tags($request->request->get('promocode', '')));

        if (!$formData) {
            return null;
        }

        if (!empty($formData['topic'])) {
            $problem = strip_tags(implode(', ', $formData['topic']));
        } else {
            $problem = 'Не указана проблема';
        }

        try {
            $userRequestPsiholog = new UserRequestPsiholog();
            $userRequestPsiholog->setName($formData['name']);
            $userRequestPsiholog->setEmail($formData['email']);
            $userRequestPsiholog->setPhone($formData['phone']);
            $userRequestPsiholog->setSex($formData['sex']);
            $userRequestPsiholog->setChannel($formData['channel']);
            $userRequestPsiholog->setProblem($problem);
            $userRequestPsiholog->setConsultationType($formData['type']);
            $userRequestPsiholog->setPromocode($formData['promocode']);
            $userRequestPsiholog->setPrice($formData['price']);
            $userRequestPsiholog->setPsihologId($formData['psihologId'] ?? null);
            $userRequestPsiholog->setLgbtq((int) ($formData['lgbtq'] ?? 0));
            $this->em->persist($userRequestPsiholog);
            $this->em->flush();

            $this->telegramNotifier->notifyAdminsPsihologRequested($userRequestPsiholog);
        } catch (\Exception $e) {
            captureException($e);

            return null;
        }

        //todo: sent telegram msg

        return $userRequestPsiholog;
    }

    private function getPrices(Request $request, Psiholog $psiholog): array
    {
        $prices = [];
        $slugPrice = $this->getSlugPrice($request);
        if ($slugPrice) {
            if ($slugPrice->isIndividual()) {
                $prices['priceIndividual'] = $slugPrice;
                $prices['priceCouple'] = $this->getPriceCouple($psiholog);
            } else {
                if ($slugPrice->isCouple()) {
                    $prices['priceIndividual'] = $this->getPriceIndividual($psiholog);
                    $prices['priceCouple'] = $slugPrice;
                } else {
                    $prices['priceIndividual'] = $this->getPriceIndividual($psiholog);
                    $prices['priceCouple'] = $this->getPriceCouple($psiholog);
                }
            }
        } else {
            $prices['priceIndividual'] = $this->getPriceIndividual($psiholog);
            $prices['priceCouple'] = $this->getPriceCouple($psiholog);
        }

        return array_filter($prices);
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

    private function getPriceIndividual(Psiholog $psiholog): ?PsihologPrice
    {
        return $this->psihologPriceRepository->findCurrent(
            $psiholog,
            Currency::UAH,
            PsihologPrice::TYPE_INDIVIDUAL,
        );
    }

    private function getPriceCouple(Psiholog $psiholog): ?PsihologPrice
    {
        return $this->psihologPriceRepository->findCurrent(
            $psiholog,
            Currency::UAH,
            PsihologPrice::TYPE_COUPLE,
        );
    }
}