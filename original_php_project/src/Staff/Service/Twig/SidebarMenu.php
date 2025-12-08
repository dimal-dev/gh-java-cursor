<?php

namespace App\Staff\Service\Twig;

use App\Staff\Service\RouteNames;
use Twig\Extension\AbstractExtension;
use Twig\TwigFunction;

class SidebarMenu extends AbstractExtension
{
    private const SIDEBAR_MENU_ITEMS = [
        RouteNames::DASHBOARD => [
            "label" => "Главная",
            "icon" => "flaticon2-protection",
        ],
        RouteNames::THERAPISTS => [
            "label" => "Терапевты",
            "icon" => "flaticon-network",
        ],
        RouteNames::ADD_THERAPIST => [
            "label" => "Добавить терапевта",
            "icon" => "",
        ],
        RouteNames::PAYOUTS => [
            "label" => "Выплаты терапевтам",
            "icon" => "",
        ],
        RouteNames::BLOG_POST_LIST => [
            "label" => "Блог",
            "icon" => "",
        ],
    ];

    public function getFunctions()
    {
        return [
            new TwigFunction('staffGetSidebarMenuItems', function () {
                return self::SIDEBAR_MENU_ITEMS;
            })
        ];
    }
}