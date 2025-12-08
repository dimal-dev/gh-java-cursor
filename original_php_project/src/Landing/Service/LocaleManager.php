<?php

namespace App\Landing\Service;

class LocaleManager
{
    public const LOCALE_UA = 'ua';
    public const LOCALE_RU = 'ru';

    public const DEFAULT_LOCALE = self::LOCALE_UA;

    public const LOCALE_LIST = [
        self::LOCALE_UA,
        self::LOCALE_RU,
    ];
}
