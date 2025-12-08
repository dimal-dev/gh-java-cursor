<?php

declare(strict_types=1);

namespace App\Psiholog\Controller;

use App\Psiholog\Service\RouteNames;
use App\Psiholog\Repository\PsihologRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Security\Csrf\CsrfToken;
use Symfony\Component\Security\Csrf\CsrfTokenManagerInterface;
use Symfony\Component\Security\Http\Authentication\AuthenticationUtils;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/login",
    name: "login",
    methods: ["GET", "POST"]
)]
final class LoginController extends AbstractController
{
    private const CSRF_TOKEN_INTENTION = 'authenticate';

    public function __construct(
        private CsrfTokenManagerInterface $csrfTokenManager,
        private PsihologRepository $userRepository
    )
    {
    }

    public function __invoke(AuthenticationUtils $authenticationUtils, Request $request): Response
    {
        $error = $authenticationUtils->getLastAuthenticationError();
        $lastUsername = $authenticationUtils->getLastUsername();

        $autologinLink = null;

        if ($request->isMethod('post')) {
            $token = new CsrfToken(self::CSRF_TOKEN_INTENTION, $request->request->get('_csrf_token'));
            if ($this->csrfTokenManager->isTokenValid($token)) {
                $email = $request->request->get('email');
                $user = $this->userRepository->findByEmail($email);
                if ($user) {
                    $autologinLink = $this->generateUrl(RouteNames::AUTO_LOGIN, [
                        't' => $user->getPsihologAutologinToken()->getToken()
                    ]);
                }
            }
        }

        return $this->render('@psiholog/pages/login.html.twig', [
            'error' => $error,
            'last_username' => $lastUsername,
            'csrf_token_intention' => self::CSRF_TOKEN_INTENTION,
            'target_path' => $this->generateUrl(\App\Staff\Service\RouteNames::DASHBOARD),
            'username_parameter' => 'email',
            'password_parameter' => 'password',
            'autologinLink' => $autologinLink,
        ]);
    }
}
