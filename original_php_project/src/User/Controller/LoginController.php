<?php

declare(strict_types=1);

namespace App\User\Controller;

use App\Notification\Service\EmailSender;
use App\Staff\Service\RouteNames;
use App\User\Repository\UserRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Cookie;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;
use Symfony\Component\Security\Csrf\CsrfToken;
use Symfony\Component\Security\Csrf\CsrfTokenManagerInterface;
use Symfony\Component\Security\Http\Authentication\AuthenticationUtils;
use Symfony\Component\Routing\Annotation\Route;

final class LoginController extends AbstractController
{
    public const ERROR_EMAIL_DOES_NOT_EXIST = 1;
    public const ERROR_INVALID_EMAIL = 2;
    public const ERROR_FAILED_TO_SEND_EMAIL = 3;

    private const CSRF_TOKEN_INTENTION = 'authenticate';
    private const COOKIE_NAME_EMAIL_SENT = 'ffsat_g_2';

    private const REDIS_KEY_EMAIL_AUTH_SENT = 'lleas_';

    private const EMAIL_CHECK_PATTERN = '/.+@.+/';

    public function __construct(
        private CsrfTokenManagerInterface $csrfTokenManager,
        private UserRepository $userRepository,
        private \Redis $redis,
        private EmailSender $emailSender,
    )
    {
    }

    public function __invoke(AuthenticationUtils $authenticationUtils, Request $request): Response
    {
        $error = null;

        do {
            if (!$request->isMethod('post')) {
                break;
            }

            $token = new CsrfToken(self::CSRF_TOKEN_INTENTION, $request->request->get('_csrf_token'));
            if (!$this->csrfTokenManager->isTokenValid($token)) {
                break;
            }

            $email = trim((string) $request->request->get('email'));
            if (!preg_match(self::EMAIL_CHECK_PATTERN, $email)) {
                $error = self::ERROR_INVALID_EMAIL;
                break;
            }


            if ($this->isEmailSent($request) || $this->redis->exists($this->getEmailSentRedisKey($email))) {
                break;
            }

            $user = $this->userRepository->findByEmail($email);
            if (!$user) {
                $error = self::ERROR_EMAIL_DOES_NOT_EXIST;
                break;
            }

            $autologinLink = $this->generateUrl(\App\User\Service\RouteNames::AUTO_LOGIN, [
                't' => $user->getUserAutologinToken()->getToken()
            ], UrlGeneratorInterface::ABSOLUTE_URL);

            if (!$this->emailSender->sendAutologin($user, $autologinLink)) {
                $error = self::ERROR_FAILED_TO_SEND_EMAIL;
                break;
            }

            $redirectResponse = $this->redirectToRoute(\App\User\Service\RouteNames::LOGIN);

            $cookie = Cookie::create(self::COOKIE_NAME_EMAIL_SENT)
                ->withValue((string) mt_rand(1232, 145341231))
                ->withExpires(time() + 60)
                ->withDomain($request->getHost())
                ->withSecure(true);

            $redirectResponse->headers->setCookie($cookie);

            $this->redis->setEx($this->getEmailSentRedisKey($email), 60, '1');

            return $redirectResponse;

        } while (false);

        $content = [
            'error' => $error,
            'csrf_token_intention' => self::CSRF_TOKEN_INTENTION,
        ];

        $content['emailHasBeenSent'] = $this->isEmailSent($request);

        return $this->render('@user/pages/login.html.twig', $content);
    }

    public function isEmailSent(Request $request): bool
    {
        return $request->cookies->has(self::COOKIE_NAME_EMAIL_SENT);
    }

    private function getEmailSentRedisKey(string $email): string
    {
        return self::REDIS_KEY_EMAIL_AUTH_SENT . $email;
    }
}
