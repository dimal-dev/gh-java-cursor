<?php

namespace App\Staff\Controller;

use App\Blog\Entity\Post;
use FroalaEditor_Image;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route(
    "/froala-image-delete-controller",
    name: "froala_image_delete_controller",
    methods: ["POST"]
)]
final class FroalaImageDeleteController extends AbstractController
{
    public function __construct(
    ) {
    }

    public function __invoke(Request $request): Response
    {
        FroalaEditor_Image::delete($_POST['src']);

        return new Response(stripslashes(json_encode('Success')));
    }
}