<?php

namespace App\Landing\Controller;

use App\Billing\Entity\Currency;
use App\Common\Service\DateLocalizedHelper;
use App\Common\Service\TimezoneHelper;
use App\Landing\Service\CheckoutCreator;
use App\Landing\Service\CurrentTimezoneStorage;
use App\Landing\Service\PsihologProfileInfo;
use App\Landing\Service\RouteNames;
use App\Psiholog\Entity\Psiholog;
use App\Psiholog\Entity\PsihologPrice;
use App\Psiholog\Entity\PsihologProfile;
use App\Psiholog\Entity\PsihologSchedule;
use App\Psiholog\Repository\PsihologPriceRepository;
use App\Psiholog\Repository\PsihologProfileRepository;
use App\Psiholog\Repository\PsihologRepository;
use App\Psiholog\Repository\PsihologScheduleRepository;
use App\Psiholog\Repository\PsihologSettingsRepository;
use App\User\Repository\PromocodeRepository;
use App\User\Repository\UserPromocodeRepository;
use App\User\Service\TimeHelper;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpKernel\Exception\NotFoundHttpException;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;

#[Route(
    "/book-consultation-apply-promocode",
    name: "book_consultation_apply_promocode",
    methods: ["POST"]
)]
final class BookConsultationApplyPromocodeController extends AbstractController
{
    public const ERROR_PROMOCODE_NOT_EXIST = 1;
//    public const ERROR_EMAIL_NOT_EXIST = 2;
    public const ERROR_EXCEEDS_NUMBER_OF_USES = 3;

    public function __construct(
        private PromocodeRepository $promocodeRepository,
        private UserPromocodeRepository $userPromocodeRepository,
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $promocodeName = strtolower((string) $request->request->get('promocode'));
        if (empty($promocodeName)) {
            return new JsonResponse([
                'error' => self::ERROR_PROMOCODE_NOT_EXIST
            ]);
        }
        $email = (string) $request->request->get('email');
//        if (empty($email)) {
//            return new JsonResponse([
//                'error' => self::ERROR_EMAIL_NOT_EXIST
//            ]);
//        }

        $promocode = $this->promocodeRepository->findByName($promocodeName);
        if (!$promocode) {
            return new JsonResponse([
                'error' => self::ERROR_PROMOCODE_NOT_EXIST
            ]);
        }

        $maxUseNumber = $promocode->getMaxUseNumber();
        if ($maxUseNumber > 0) {
            $existingNumberOfUses = $this->userPromocodeRepository->countNumberOfUses($email, $promocode->getId());
            if ($existingNumberOfUses >= $maxUseNumber) {
                return new JsonResponse([
                    'error' => self::ERROR_EXCEEDS_NUMBER_OF_USES
                ]);
            }
        }

        return new JsonResponse([
            'fs' => $promocode->getId(),
            'discountPercent' => $promocode->getDiscountPercent(),
        ]);
    }
}