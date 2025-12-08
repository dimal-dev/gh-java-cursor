<?php

namespace App\Common\Service\Twig;

use App\Common\Service\TimezoneHelper;
use DateTime;
use DateTimeZone;
use Symfony\Component\HttpFoundation\RequestStack;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;
use Twig\Extension\AbstractExtension;
use Twig\TwigFunction;

final class LocalizeExtension extends AbstractExtension
{
    public function __construct(
        private UrlGeneratorInterface $urlGenerator,
        private RequestStack $requestStack,
    ) {
    }

    public function getFunctions()
    {
        return [
            new TwigFunction('currentRouteLocalized', function (array $params) {
                return $this->getCurrentRouteLocalized($params);
            }),
            new TwigFunction('currentRouteLocalizedFull', function (array $params) {
                return $this->getCurrentRouteLocalized($params, UrlGeneratorInterface::ABSOLUTE_URL);
            }),
        ];
    }

    private function getCurrentRouteLocalized(array $params, int $referenceType = UrlGeneratorInterface::ABSOLUTE_PATH)
    {
        $request = $this->requestStack->getCurrentRequest();
        $route = $request->get('_route');
        if (empty($route)) {
            return '';
        }
        $routeParams = $request->get('_route_params') ?? [];
        $urlParams = array_merge(
            $request->query->all(),
            $routeParams,
            $params
        );

        return $this->urlGenerator->generate($request->get('_route'), $urlParams, $referenceType);
    }
}