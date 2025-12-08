<?php

namespace App\Psiholog\Service\Twig;

use App\Psiholog\Entity\PsihologProfile;
use App\Psiholog\Repository\PsihologProfileRepository;
use App\Psiholog\Service\CurrentPsihologRetriever;
use Twig\Extension\AbstractExtension;
use Twig\TwigFunction;

class Psiholog extends AbstractExtension
{
    private ?PsihologProfile $psihologProfile = null;

    public function __construct(
        private CurrentPsihologRetriever $currentPsihologRetriever,
        private PsihologProfileRepository $psihologProfileRepository,
    ) {
    }

    public function getFunctions()
    {
        return [
            new TwigFunction('getCurrentPsihologFullName', function () {
                if (!$this->psihologProfile) {
                    $psiholog = $this->currentPsihologRetriever->get();
                    $this->psihologProfile = $this->psihologProfileRepository->findByPsiholog($psiholog);
                }

                return $this->psihologProfile->getFullName();
            }),
            new TwigFunction('getCurrentPsiholog', function () {
                return $this->currentPsihologRetriever->get();
            }),
        ];
    }
}