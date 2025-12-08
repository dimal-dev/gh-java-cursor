<?php

namespace App\Staff\Controller;

use App\Blog\Entity\Post;
use FroalaEditor_Image;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/froala-image-upload-controller",
    name: "froala_image_upload_controller",
    methods: ["POST"]
)]
final class FroalaImageUploadController extends AbstractController
{
    public function __construct(
    ) {
    }

    public function __invoke(Request $request): Response
    {

        $folderName = '/upload/';
        $str = substr(md5(random_bytes(10)), 0, 2);
        $path = str_split($str);
        $path = implode('/', $path);
        $path = $folderName . $path . '/';
        $fullPath = $_SERVER['DOCUMENT_ROOT'] . $path;
        if (!file_exists($fullPath)) {
            mkdir($fullPath, 0777, true);
        }
        $response = FroalaEditor_Image::upload($path);

        return new Response(stripslashes(json_encode($response)));
    }
}