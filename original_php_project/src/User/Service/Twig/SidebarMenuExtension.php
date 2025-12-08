<?php

namespace App\User\Service\Twig;

use App\User\Service\RouteNames;
use Twig\Extension\AbstractExtension;
use Twig\TwigFunction;

class SidebarMenuExtension extends AbstractExtension
{
    private const SIDEBAR_MENU_ITEMS = [
        RouteNames::DASHBOARD => [
            "label" => "Главная",
            "icon" => "flaticon2-protection",
        ],
        RouteNames::CHAT => [
            "label" => "Чат с психологом",
            "icon" => "flaticon-chat",
            "id" => 'sidebar-menu-item-chat'
        ],
//        RouteNames::FAQ => [
//            "label" => "Частые вопросы",
//            "icon" => "flaticon-questions-circular-button",
//        ],
        RouteNames::RULES => [
            "label" => "Условия&nbsp;консультаций",
            "icon" => "flaticon2-graph-1",
        ],
//        RouteNames::WALLET => [
//            "label" => "Кошелек",
//            "icon" => "flaticon-coins",
//        ],
        RouteNames::SETTINGS => [
            "label" => "Настройки",
            "icon" => "flaticon2-user-1",
        ],
    ];

    public function getFunctions()
    {
        return [
            new TwigFunction('userGetSidebarMenuItems', function () {
                return self::SIDEBAR_MENU_ITEMS;
            })
        ];
    }
}