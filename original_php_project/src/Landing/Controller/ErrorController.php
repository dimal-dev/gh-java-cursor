<?php

namespace App\Landing\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\ErrorHandler\Exception\FlattenException;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;

final class ErrorController extends AbstractController
{
    public function __construct()
    {
    }

    public function __invoke(
        Request $request,
        FlattenException $exception,
    ): Response {
        $statusCode = $exception->getStatusCode();

        $template = $statusCode === Response::HTTP_NOT_FOUND ? '404' : '500';

        $viewPath = "@landing/pages/error/{$template}.html.twig";

        $response = $this->render($viewPath);
        $response->setStatusCode($statusCode);

        return $response;
    }
}