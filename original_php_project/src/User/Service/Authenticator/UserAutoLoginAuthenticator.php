<?php

declare(strict_types=1);

namespace App\User\Service\Authenticator;

use App\User\Entity\SecurityUser;
use App\User\Repository\SecurityUserProviderRepository;
use App\User\Service\RouteNames;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;
use Symfony\Component\Security\Core\Exception\AuthenticationException;
use Symfony\Component\Security\Core\Exception\BadCredentialsException;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Security\Core\User\UserProviderInterface;
use Symfony\Component\Security\Core\User\UserInterface;
use Symfony\Component\Security\Core\Authentication\Token\TokenInterface;
use Symfony\Component\Security\Guard\AbstractGuardAuthenticator;

final class UserAutoLoginAuthenticator extends AbstractGuardAuthenticator
{
    public const REDIRECT_TO_PARAM_NAME = 'rtfa';

    public const TOKEN_PARAM_NAME = 't';

    private SecurityUserProviderRepository $securityUserProviderRepository;
    private UrlGeneratorInterface $urlGenerator;

    public function __construct(
        SecurityUserProviderRepository $securityUserProviderRepository,
        UrlGeneratorInterface $urlGenerator
    ) {
        $this->securityUserProviderRepository = $securityUserProviderRepository;
        $this->urlGenerator = $urlGenerator;
    }

    public function supports(Request $request): bool
    {
        return RouteNames::AUTO_LOGIN === $request->attributes->get('_route')
            && $request->isMethod('GET')
            && $request->query->get(self::TOKEN_PARAM_NAME);
    }

    public function getCredentials(Request $request): array
    {
        return [
            'token' => $request->query->get(self::TOKEN_PARAM_NAME),
        ];
    }

    public function getUser($credentials, UserProviderInterface $userProvider): ?SecurityUser
    {
        $user = $this->securityUserProviderRepository->findByToken($credentials['token']);

        if (!$user) {
            throw new BadCredentialsException();
        }

        return $user;
    }

    public function checkCredentials($credentials, UserInterface $user): bool
    {
        return true;
    }

    public function onAuthenticationSuccess(Request $request, TokenInterface $token, $providerKey)
    {
        $query = $request->query->all();
        unset($query[self::TOKEN_PARAM_NAME]);

        if ($request->query->has(self::REDIRECT_TO_PARAM_NAME)) {
            $url = $request->query->get(self::REDIRECT_TO_PARAM_NAME);

            return new RedirectResponse($url);
        }

        $userSpaceUrl = $this->urlGenerator->generate(RouteNames::DASHBOARD, $query);

        return new RedirectResponse($userSpaceUrl);
    }

    public function start(Request $request, AuthenticationException $authException = null)
    {
        return new RedirectResponse($this->urlGenerator->generate(RouteNames::LOGIN));
    }

    public function onAuthenticationFailure(Request $request, AuthenticationException $exception)
    {
        return new RedirectResponse($this->urlGenerator->generate(RouteNames::LOGIN));
    }

    public function supportsRememberMe()
    {
        return true;
    }
}
