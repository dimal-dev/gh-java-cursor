<?php

namespace App\Landing\Service;

class RouteNames
{
    public const MAIN_PAGE = self::PREFIX . 'main_page';
    public const LOGIN = self::PREFIX . 'login';
    public const PRICES = self::PREFIX . 'prices';
    public const CONSULTATION_CONDITIONS = self::PREFIX . 'consultation_conditions';
    public const BLOG_POST_LIST = self::PREFIX . 'blog_post_list';
    public const BLOG_POST_VIEW = self::PREFIX . 'blog_post_view';

    public const BOOK_CONSULTATION_APPLY_PROMOCODE = self::PREFIX . 'book_consultation_apply_promocode';

    public const PSIHOLOG_LIST = self::PREFIX . 'psiholog_list';
    public const PSIHOLOG_PROFILE = self::PREFIX . 'psiholog_profile';
    public const TERMS_OF_USE = self::PREFIX . 'terms_of_use';
    public const PRIVACY = self::PREFIX . 'privacy';
    public const REFUND_POLICY = self::PREFIX . 'refund_policy';
    public const FAMILY_PSIHOLOG = self::PREFIX . 'family_psiholog';
    public const TEENAGE_PSIHOLOG = self::PREFIX . 'teenage_psiholog';
    public const ABOUT = self::PREFIX . 'about';
    public const CHECKOUT = self::PREFIX . 'checkout';
    public const CHECKOUT_THANK_YOU = self::PREFIX . 'checkout_thank_you';
    public const CHECKOUT_THANK_YOU_CHECK_STATUS_AJAX = self::PREFIX . 'checkout_thank_you_check_status_ajax';
    public const REQUEST_PSIHOLOG = self::PREFIX . 'request_psiholog';
    public const REQUEST_PSIHOLOG_SUCCESS = self::PREFIX . 'request_psiholog_success';

    public const BOOK_CONSULTATION_FROM_USER_BALANCE = self::PREFIX . 'book_consultation_from_user_balance';
    public const BOOK_CONSULTATION = self::PREFIX . 'book_consultation';

    public const SAVE_USER_TIMEZONE = self::PREFIX . 'save_user_timezone';

    private const PREFIX = 'landing_';
}
