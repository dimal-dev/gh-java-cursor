<?php

namespace App\Landing\Controller;

use App\Landing\Repository\UserRequestPsihologRepository;
use App\Landing\Service\RouteNames;
use App\Psiholog\Repository\PsihologProfileRepository;
use Hashids\Hashids;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/request-psiholog-success",
    name: "request_psiholog_success",
    methods: ["GET"]
)]
final class RequestPsihologSuccessController extends AbstractController
{
    public function __construct(
        private PsihologProfileRepository $psihologProfileRepository,
        private Hashids $hashids,
        private UserRequestPsihologRepository $userRequestPsihologRepository
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $content = [];
        $content['headerWithoutShadow'] = true;

        if ($request->query->has('id')) {
            $psihologProfile = $this->psihologProfileRepository->findByPsihologId((int) $request->query->get('id'));
            if ($psihologProfile) {
                $content['psihologProfile'] = $psihologProfile;
            }
        }

        $userRequestPsihologIdEncoded = $request->cookies->get(RequestPsihologController::USER_REQUEST_PSIHOLOG_COOKIE_NAME);
        if ($userRequestPsihologIdEncoded) {
            $userRequestPsihologId = $this->hashids->decode($userRequestPsihologIdEncoded);
            if ($userRequestPsihologId) {
                $userRequestPsihologId = reset($userRequestPsihologId);
                $userRequestPsiholog = $this->userRequestPsihologRepository->findById($userRequestPsihologId);
                $content['clientName'] = $userRequestPsiholog->getName();
            }
        }

        $content['subsequent'] = (bool) $request->query->get('subsequent', false);

        return $this->render('@landing/pages/request-psiholog-success.html.twig', $content);
    }
}