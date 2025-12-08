<?php

namespace App\User\Controller;

use App\Billing\Repository\OrderRepository;
use App\Billing\Repository\UserWalletRepository;
use App\Common\Service\ArrayIndexer;
use App\Common\Service\TimezoneHelper;
use App\Landing\Service\RouteNames;
use App\Psiholog\Repository\PsihologProfileRepository;
use App\User\Entity\User;
use App\User\Repository\UserConsultationRepository;
use App\User\Service\TimeHelper;
use DateTimeZone;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;
use Symfony\Component\Security\Core\Security;

#[Route(
    "/",
    name: "dashboard",
    methods: ["GET"]
)]
final class IndexController extends AbstractController
{

    public function __construct(
        private Security $security,
        private UrlGeneratorInterface $urlGenerator,
        private TimezoneHelper $timezoneHelper,
        private UserConsultationRepository $userConsultationRepository,
        private PsihologProfileRepository $psihologProfileRepository,
        private ArrayIndexer $arrayIndexer,
        private TimeHelper $timeHelper,
        private OrderRepository $orderRepository,
        private UserWalletRepository $userWalletRepository,
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $redirectResponse = $this->mustRebookRedirectResponse($request);
        if ($redirectResponse) {
            return $redirectResponse;
        }

        /** @var User $user */
        $user = $this->security->getUser()->getUser();
        $userTimezone = new DateTimeZone($user->getTimezone());
        $userTime = new \DateTime("now", $userTimezone);

        $this->timeHelper->setTimezone($userTimezone);

        $upcomingConsultations = $this->getUpcomingConsultations($user->getId());
        $closestConsultation = reset($upcomingConsultations);

        $content = [];
        if ($closestConsultation) {
            array_shift($upcomingConsultations);
            $content['closestConsultation'] = $closestConsultation;
            $content['latestPsiholog'] = $closestConsultation;
        } else {
            $content['latestPsiholog'] = $this->getLatestPsiholog($user->getId());
        }
        $content['upcomingConsultations'] = $upcomingConsultations;
        $content['timezone'] = $user->getTimezone();
        $content['timezoneOffset'] = $userTime->getOffset();
        $content['timezoneLabel'] = $this->timezoneHelper->getLabelForOffset(
            $userTime->getOffset(),
            $user->getTimezone()
        );
        $content['userWallet'] = $this->userWalletRepository->findByUser($user);
        $content['isFullNameSetByUser'] = $user->isFullNameSetByUser();
        $content['rulesWereRead'] = $request->cookies->has(RulesController::COOKIE_NAME_RULES_WAS_READ);
        $content['showSuccessfullyBookedConsultationMessage'] = (bool) $request->query->get('show-successfully-booked-consultation-message', 0);

        return $this->render('@user/pages/index.html.twig', $content);
    }

    private function getUpcomingConsultations(int $userId): array
    {
        $upcomingConsultations = $this->userConsultationRepository->findUpcoming($userId);
        if (empty($upcomingConsultations)) {
            return [];
        }

        $psihologIdList = array_column($upcomingConsultations, 'psiholog_id', 'psiholog_id');
        $psihologProfileList = $this->psihologProfileRepository->findAllByIdList($psihologIdList);
        $psihologProfileListIndexed = $this->arrayIndexer->byKeyUnique($psihologProfileList, 'psiholog_id');

        $upcomingConsultationsIndexed = [];
        foreach ($upcomingConsultations as $consultation) {
            if (isset($upcomingConsultationsIndexed[$consultation['id']])) {
                continue;
            }

            $psihologProfile = $psihologProfileListIndexed[$consultation['psiholog_id']];
            $consultation += $psihologProfile;

            $consultation['available_at_tz_aware'] = $this->timeHelper->toUserTz($consultation['available_at']);

            $upcomingConsultationsIndexed[$consultation['id']] = $consultation;

        }

        return $upcomingConsultationsIndexed;
    }

    private function getLatestPsiholog(int $userId): ?array
    {
        $latestPsiholog = $this->userConsultationRepository->findLatest($userId);
        if (empty($latestPsiholog)) {
            $psihologId = $this->orderRepository->findUserLatestBoughtPsihologId($userId);
            if (!$psihologId) {
                return null;
            }
            $latestPsiholog = [
                'psiholog_id' => $psihologId,
            ];
        }

        $psihologProfileList = $this->psihologProfileRepository->findAllByIdList(
            [$latestPsiholog['psiholog_id']]
        );

        $latestPsiholog += $psihologProfileList[0];

        return $latestPsiholog;
    }

    private function mustRebookRedirectResponse(Request $request): ?RedirectResponse
    {
        return null;
        $isConsultationBooked = $request->query->get('isConsultationBooked');
        $consultationBooked = ($isConsultationBooked === null || $isConsultationBooked != 0);
        if ($consultationBooked) {
            return null;
        }

        $psihologId = $request->query->get('psihologId');
        if ($psihologId === null) {
            return null;
        }

        $url = $this->urlGenerator->generate(RouteNames::PSIHOLOG_PROFILE, [
            'id' => $psihologId,
            'failedToBook' => 1,
        ]);
        $url .= '#failed-to-book';

        return new RedirectResponse($url);
    }
}