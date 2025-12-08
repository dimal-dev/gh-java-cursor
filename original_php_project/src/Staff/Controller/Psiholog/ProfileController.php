<?php

namespace App\Staff\Controller\Psiholog;

use DateTime;
use DateTimeZone;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/psiholog/profile",
    name: "psiholog_profile",
    methods: ["GET"]
)]
final class ProfileController extends AbstractController
{

    public function __construct(
    ) {
    }

    public function __invoke(Request $request): Response
    {
        $content = [];
        $id = (int) $request->query->get('id', 0);

        $content['id'] = $id;

        return $this->render('@staff/pages/psiholog/profile.html.twig', $content);
    }
}