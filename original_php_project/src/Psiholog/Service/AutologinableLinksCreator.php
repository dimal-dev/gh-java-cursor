<?php

namespace App\Psiholog\Service;

use App\Psiholog\Entity\Psiholog;
use App\Psiholog\Service\Authenticator\PsihologAutoLoginAuthenticator;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;

class AutologinableLinksCreator
{
    public function __construct(
        private UrlGeneratorInterface $urlGenerator
    ) {
    }

    public function create(Psiholog $psiholog, string $routeName, array $params = []): string
    {
        $link = $this->urlGenerator->generate($routeName, $params);

        return $this->createAutologinableLink($link, $psiholog);
    }

    private function createAutologinableLink(string $link, Psiholog $psiholog): string
    {
        $token = $psiholog->getPsihologAutologinToken()->getToken();

        return $this->urlGenerator->generate(RouteNames::AUTO_LOGIN, [
            PsihologAutoLoginAuthenticator::TOKEN_PARAM_NAME => $token,
            PsihologAutoLoginAuthenticator::REDIRECT_TO_PARAM_NAME => $link,
        ], UrlGeneratorInterface::ABSOLUTE_URL);
    }
}
