<?php

namespace App\Landing\Service;

use App\Common\Service\ArrayIndexer;
use App\Psiholog\Repository\PsihologPriceRepository;
use App\Psiholog\Repository\PsihologProfileRepository;
use Symfony\Component\Asset\Packages;

class PsihologList
{
    private const PACKAGE_NAME = 'landing';

    public function __construct(
        private Packages $packages,
        private PsihologProfileInfo $psihologProfile,
        private ArrayIndexer $arrayIndexer,
        private PsihologProfileRepository $psihologProfileRepository,
        private PsihologPriceRepository $psihologPriceRepository,
    )
    {
    }

    public function getPublicPsihologList(): array
    {
        return $this->getPsihologList($this->psihologProfile->getAll());
    }

    public function getFamilyPsihologList(): array
    {
        return $this->getPsihologList($this->psihologProfile->getFamily());
    }

    public function getTeenagerPsihologList(): array
    {
        return $this->getPsihologList($this->psihologProfile->getTeenager());
    }


    private function getPsihologList(array $psihologConfig): array
    {
        $psihologList = [];
        $psihologIdList = array_keys($psihologConfig);

        $profiles = $this->getProfiles($psihologIdList);
        $prices = $this->getPrices($psihologIdList);

        foreach ($psihologConfig as $psihologId => $row) {
            $psihologPrices = $prices[$psihologId];
            if (count($psihologPrices) === 1) {
                $row['price'] = $psihologPrices[0]['price'];
                $row['price_from'] = false;
            } else {
                usort($psihologPrices, fn($p1, $p2) => $p1['price'] <=> $p2['price']);

                $row['price'] = reset($psihologPrices)['price'];
                $row['price_from'] = true;
            }

            $profile = $profiles[$psihologId];
            $row['fullName'] = "{$profile['first_name']} {$profile['last_name']}";
            $row['profileUrl'] = $this->packages->getUrl(
                "img/select-psiholog/{$profile['profile_template']}.jpg",
                self::PACKAGE_NAME
            );

            $row['id'] = $psihologId;
            $row['currency'] = '₴';

            $psihologList[] = $row;
        }

        return $psihologList;
    }

    private function getProfiles(array $psihologIdList): array
    {
        $list = $this->psihologProfileRepository->findAllByIdList($psihologIdList);

        return $this->arrayIndexer->byKeyUnique($list, 'psiholog_id');
    }

    private function getPrices(array $psihologIdList): array
    {
        $list = $this->psihologPriceRepository->findAllCurrentByIdList($psihologIdList);

        return $this->arrayIndexer->byKey($list, 'psiholog_id');
    }
}
