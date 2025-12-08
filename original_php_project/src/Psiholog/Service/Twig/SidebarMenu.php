<?php

namespace App\Psiholog\Service\Twig;

use App\Psiholog\Service\RouteNames;
use Twig\Extension\AbstractExtension;
use Twig\TwigFunction;

class SidebarMenu extends AbstractExtension
{
    private const SIDEBAR_MENU_ITEMS = [
//        RouteNames::DASHBOARD => [
//            "label" => "Главная",
//            "icon" => "flaticon2-protection",
//        ],
        RouteNames::SCHEDULE => [
            "label" => "Консультации",
            "icon" => "flaticon-clock-2",
        ],
        RouteNames::SCHEDULE_SETTINGS => [
            "label" => "Укажите ваш график",
            "icon" => "flaticon-calendar-with-a-clock-time-tools",
        ],
        RouteNames::NEW_MESSAGES => [
            "label" => "Новые сообщения",
            "icon" => "flaticon-chat",
            "id" => 'sidebar-menu-item-chat'
        ],
//        RouteNames::CLIENTS => [
//            "label" => "Клиенты",
//            "icon" => "flaticon-network",
//        ],
//        RouteNames::PAYMENTS => [
//            "label" => "Выплаты ₴",
//            "icon" => "flaticon-coins",
//        ],
//        RouteNames::PUBLIC_PROFILE => [
//            "label" => "Ваша анкета",
//            "icon" => "flaticon-profile",
//        ],
        RouteNames::SETTINGS => [
            "label" => "Настройки",
            "icon" => "flaticon2-user-1",
        ],
    ];

    public function getFunctions()
    {
        return [
            new TwigFunction('psihologGetSidebarMenuItems', function () {
                return self::SIDEBAR_MENU_ITEMS;
            })
        ];
    }
}