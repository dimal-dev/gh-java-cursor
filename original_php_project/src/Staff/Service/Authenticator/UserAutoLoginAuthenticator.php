<?php

declare(strict_types=1);

namespace App\Staff\Service\Authenticator;

use App\Staff\Entity\SecurityUser;
use App\Staff\Repository\SecurityUserProviderRepository;
use App\Staff\Service\RouteNames;
use Symfony\Component\HttpFoundation\RedirectResponse;
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
            && $request->query->get('t');
    }

    public function getCredentials(Request $request): array
    {
        return [
            'token' => $request->query->get('t'),
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
        $userSpaceUrl = $this->urlGenerator->generate(RouteNames::DASHBOARD);

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
