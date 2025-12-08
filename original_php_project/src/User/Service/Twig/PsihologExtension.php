<?php

namespace App\User\Service\Twig;

use App\Common\Service\ArrayIndexer;
use App\Psiholog\Repository\PsihologProfileRepository;
use Symfony\Component\Asset\Packages;
use Twig\Extension\AbstractExtension;
use Twig\TwigFunction;

class PsihologExtension extends AbstractExtension
{
    private ?array $psihologProfile = null;

    public function __construct(
        private PsihologProfileRepository $psihologProfileRepository,
        private ArrayIndexer $arrayIndexer,
        private Packages $packages,
    )
    {
    }

    public function getFunctions()
    {
        return [
            new TwigFunction('getPsihologProfileSrc', function (int $psihologId) {
                $this->setupPsihologCache($psihologId);
                $template = $this->psihologProfile[$psihologId]['profile_template'];

                return $this->packages->getUrl("media/psiholog-profile/{$template}.jpg", 'user');
            }),
            new TwigFunction('getPsihologFullName', function (int $psihologId) {
                $this->setupPsihologCache($psihologId);

                $psihologProfile = $this->psihologProfile[$psihologId];

                return "{$psihologProfile['first_name']} {$psihologProfile['last_name']}";

            }),
        ];
    }

    public function setupPsihologCache(int $psihologId): void
    {
        if (!isset($this->psihologProfile[$psihologId])) {
            $this->psihologProfile[$psihologId] = $this->psihologProfileRepository->findAllByIdList([$psihologId])[0];
        }
    }
}