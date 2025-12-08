<?php

namespace App\Psiholog\Service;

class RouteNames
{
    public const LOGIN = self::PREFIX . 'login';
    public const AUTO_LOGIN = self::PREFIX . 'auto_login';
    public const LOGOUT = self::PREFIX . 'logout';

    public const DASHBOARD = self::PREFIX . 'dashboard';
    public const SETTINGS = self::PREFIX . 'settings';
    public const CLIENTS = self::PREFIX . 'clients';
    public const CLIENTS_LIST_AJAX = self::PREFIX . 'clients_list_ajax';

    public const SCHEDULE = self::PREFIX . 'schedule';
    public const SCHEDULE_SETTINGS = self::PREFIX . 'schedule_settings';
    public const SCHEDULE_SETTINGS_WEEK = self::PREFIX . 'schedule_settings_week';
    public const SCHEDULE_SETTINGS_BOOK_TIME_AJAX = self::PREFIX . 'schedule_settings_book_time_ajax';

    public const NEW_MESSAGES = self::PREFIX . 'new_messages';
    public const NEW_MESSAGES_LIST_AJAX = self::PREFIX . 'new_messages_list_ajax';
    public const PAYMENTS = self::PREFIX . 'payments';
    public const PAYMENTS_HISTORY = self::PREFIX . 'payments_history';
    public const PAYMENTS_HISTORY_ITEMS_AJAX = self::PREFIX . 'payments_history_items_ajax';
    public const PUBLIC_PROFILE = self::PREFIX . 'public_profile';
    public const SCHEDULE_LIST_AJAX = self::PREFIX . 'schedule_list_ajax';

    public const CHAT = self::PREFIX . 'chat';
    public const CHAT_GET_UNREAD_MESSAGES_AMOUNT_AJAX = self::PREFIX . 'chat_get_unread_messages_amount_ajax';
    public const CHAT_GET_MESSAGES_AJAX = self::PREFIX . 'chat_get_messages_ajax';
    public const CHAT_SEND_MESSAGE = self::PREFIX . 'chat_send_message';

    public const USER_NOTES = self::PREFIX . 'user_notes';

    public const CANCEL_CONSULTATION = self::PREFIX . 'cancel_consultation';

    private const PREFIX = 'psiholog_';
}