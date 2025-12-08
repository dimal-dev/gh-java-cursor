<?php

declare(strict_types=1);

namespace App\Staff\Controller;

use App\Staff\Repository\UserRepository;
use App\Staff\Service\RouteNames;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Security\Csrf\CsrfToken;
use Symfony\Component\Security\Csrf\CsrfTokenManagerInterface;
use Symfony\Component\Security\Http\Authentication\AuthenticationUtils;
use Symfony\Component\Routing\Annotation\Route;

/**
 * @Route(
 *     path="/login",
 *     name="login",
 *     methods={"GET", "POST"}
 * )
 */
final class LoginController extends AbstractController
{
    private const CSRF_TOKEN_INTENTION = 'authenticate';

    public function __construct(
        private CsrfTokenManagerInterface $csrfTokenManager,
        private UserRepository $userRepository
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
                        't' => $user->getUserAutologinToken()->getToken()
                    ]);
                }
            }
        }

        return $this->render('@staff/pages/login.html.twig', [
            'error' => $error,
            'last_username' => $lastUsername,
            'csrf_token_intention' => 'authenticate',
            'target_path' => $this->generateUrl(RouteNames::DASHBOARD),
            'username_parameter' => 'email',
            'autologinLink' => $autologinLink,
        ]);
    }
}
