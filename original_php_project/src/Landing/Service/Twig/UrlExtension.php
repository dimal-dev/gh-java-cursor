<?php

namespace App\Landing\Service\Twig;

use App\Landing\Service\CurrentTimezoneStorage;
use Symfony\Component\HttpFoundation\RequestStack;
use Symfony\Component\Security\Core\Security;
use Twig\Extension\AbstractExtension;
use Twig\TwigFunction;

class UrlExtension extends AbstractExtension
{
    public function __construct(
        private RequestStack $requestStack,
    ) {
    }

    public function getFunctions()
    {
        return [
            new TwigFunction('getCanonicalUrl', function () {
                $request = $this->requestStack->getCurrentRequest();
                $uri = $request->getUri();
                $uriParts = parse_url($uri);

                return "{$uriParts['scheme']}://{$uriParts['host']}{$uriParts['path']}";
            }),
        ];
    }
}