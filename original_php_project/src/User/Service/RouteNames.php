<?php

namespace App\User\Service;

class RouteNames
{
    public const LOGIN = self::PREFIX . 'login';
    public const LOGOUT = self::PREFIX . 'logout';

    public const DASHBOARD = self::PREFIX . 'dashboard';
    public const AUTO_LOGIN = self::PREFIX . 'auto_login';
    public const SETTINGS = self::PREFIX . 'settings';
    public const MY_THERAPIST = self::PREFIX . 'my_psiholog';
    public const WALLET = self::PREFIX . 'wallet';
    public const FAQ = self::PREFIX . 'faq';

    public const CHAT = self::PREFIX . 'chat';
    public const CHAT_GET_MESSAGES_AJAX = self::PREFIX . 'chat_get_messages_ajax';
    public const CHAT_GET_UNREAD_MESSAGES_AMOUNT_AJAX = self::PREFIX . 'chat_get_unread_messages_amount_ajax';
    public const CHAT_SEND_MESSAGE = self::PREFIX . 'chat_send_message';

    public const RULES = self::PREFIX . 'rules';
    public const THERAPY_DESCRIPTION = self::PREFIX . 'therapy_description';

    public const CANCEL_CONSULTATION = self::PREFIX . 'cancel_consultation';

    private const PREFIX = 'user_';
}