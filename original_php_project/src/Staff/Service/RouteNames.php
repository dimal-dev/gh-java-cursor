<?php

namespace App\Staff\Service;

class RouteNames
{
    public const LOGIN = self::PREFIX . 'login';
    public const AUTO_LOGIN = self::PREFIX . 'auto_login';
    public const LOGOUT = self::PREFIX . 'logout';

    public const DASHBOARD = self::PREFIX . 'dashboard';
    public const THERAPISTS = self::PREFIX . 'psihologs';
    public const THERAPISTS_LIST_AJAX = self::PREFIX . 'psihologs_list_ajax';
    public const ADD_THERAPIST = self::PREFIX . 'add_psiholog';
    public const THERAPIST_SETTINGS = self::PREFIX . 'psiholog_settings';

    public const THERAPIST_PROFILE = self::PREFIX . 'psiholog_profile';
    public const FROALA_IMAGE_UPLOAD_CONTROLLER = self::PREFIX . 'froala_image_upload_controller';
    public const FROALA_IMAGE_DELETE_CONTROLLER = self::PREFIX . 'froala_image_delete_controller';

    public const PAYOUTS = self::PREFIX . 'payouts';
    public const PAYOUTS_LIST_AJAX = self::PREFIX . 'payouts_list_ajax';


    public const BLOG_POST_ADD = self::PREFIX . 'blog_post_add';
    public const BLOG_POST_LIST = self::PREFIX . 'blog_post_list';
    public const BLOG_POST_EDIT = self::PREFIX . 'blog_post_edit';
    public const BLOG_POST_LIST_DATA_AJAX = self::PREFIX . 'blog_post_list_data_ajax';

    private const PREFIX = 'staff_';
}