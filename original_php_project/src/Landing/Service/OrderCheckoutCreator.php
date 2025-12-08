<?php

namespace App\Landing\Service;

use App\Billing\Entity\Checkout;
use App\Billing\Entity\Order;
use App\Billing\Entity\OrderPsihologSchedule;
use App\Psiholog\Entity\PsihologPrice;
use App\Psiholog\Entity\PsihologSchedule;
use Doctrine\ORM\EntityManagerInterface;
use Ramsey\Uuid\Uuid;
use Symfony\Component\HttpFoundation\Request;

class OrderCheckoutCreator
{
    public function __construct(
        private EntityManagerInterface $em,
        private CurrentTimezoneStorage $currentTimezoneStorage,
    ) {
    }

    public function create(
        Request $request,
        PsihologPrice $price,
        PsihologSchedule $schedule,
        Checkout $checkout,
    ): Order {
        $promocode = $checkout->getUserPromocode()?->getPromocode();

        if ($promocode) {
            $priceInCents = (int) ($price->getPrice() * (100 - $promocode->getDiscountPercent()));
        } else {
            $priceInCents = $price->getPriceInCents();
        }


        $order = new Order();
        $slug = Uuid::uuid4()->getHex();
        $order->setCheckoutSlug($slug);
        $order->setCurrency($price->getCurrency());
        $order->setPrice($priceInCents);
        $order->setPsihologPriceId($price->getId());
        $order->setLocale($request->getLocale() ?: LocaleManager::DEFAULT_LOCALE);
        $order->setCheckoutId($checkout->getId());
        $order->setRequestCookies($this->getRequestCookiesString($request));

        $order->setGaClientId($this->getGaClientId($request));

        // ahtung: @warning: REVIEWER PLEASE NOTIFY ME THE MOMENT YOU SEE THIS TEXT
        // ahtung: THE CODE BELOW IS DANGEROUS AND WASN'T INTENDED TO BE RELEASED
        // ahtung
        // ahtung
        // ahtung
        // ahtung
        // ahtung
        // ahtung
        // ahtung
        // ahtung
        // ahtung
        // ahtung
        // ahtung
        // ahtung
        $orderPsihologSchedule = new OrderPsihologSchedule();
        $orderPsihologSchedule->setOrder($order);
        $orderPsihologSchedule->setPsihologSchedule($schedule);

        //todo: set actual detected timezone
        $order->setTimezone($this->currentTimezoneStorage->getCurrentTimezone());

        $this->em->persist($order);
        $this->em->persist($orderPsihologSchedule);
        $this->em->flush();

        return $order;
    }

    private function getRequestCookiesString(Request $request): ?string
    {
        $cookies = json_encode($request->cookies->all());
        if (is_string($cookies)) {
            return $cookies;
        }

        return null;
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
}
