<?php

namespace App\Landing\Service;

use App\Billing\Entity\Checkout;
use App\Psiholog\Entity\PsihologPrice;
use App\Psiholog\Entity\PsihologSchedule;
use App\User\Entity\Promocode;
use App\User\Entity\UserPromocode;
use Doctrine\ORM\EntityManagerInterface;
use Ramsey\Uuid\Uuid;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

class CheckoutCreator
{
    public function __construct(
        private EntityManagerInterface $em,
    ) {
    }

    public function create(
        Request $request,
        PsihologPrice $price,
        PsihologSchedule $schedule,
        ?Promocode $promocode,
        string $authType,
        string $email,
        ?string $name = null,
        ?string $phone = null,
    ): Checkout {
        $userPromocode = null;
        if ($promocode) {
            $userPromocode = new UserPromocode();
            $userPromocode->setPromocode($promocode);
            $userPromocode->setEmail($email);
            $userPromocode->setAppliedAt(new \DateTime());
            $userPromocode->setState($userPromocode::STATE_APPLIED);
            $this->em->persist($userPromocode);
//            $this->em->flush();
        }



        $checkout = new Checkout();
        $checkout->setPsihologPriceId($price->getId());
        $checkout->setPsihologScheduleId($schedule->getId());
        $checkout->setSlug(Uuid::uuid4()->getHex());
        $checkout->setAuthType($authType);
        $checkout->setName($name);
        $checkout->setEmail($email);
        $checkout->setPhone($phone);
        $checkout->setGaClientId($this->getGaClientId($request));
        $checkout->setGaClientIdOriginal($this->getGaClientIdOriginal($request));

        if ($userPromocode) {
            $checkout->setUserPromocode($userPromocode);
        }

        $this->em->persist($checkout);
        $this->em->flush();

        return $checkout;
    }

    private function getGaClientId(Request $request): ?string
    {
        $ga = $request->cookies->get('_ga');
        if ($ga) {
            if (preg_match('@GA\d+\.\d+\.@', $ga)) {
                return preg_replace('@GA\d+\.\d+\.@', '', $ga);
            }

            return $ga;
        }

        return null;
    }

    private function getGaClientIdOriginal(Request $request): ?string
    {
        return $request->cookies->get('_ga');
    }
}
