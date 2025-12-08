<?php

namespace App\Notification\Service;

use App\Common\Service\DateLocalizedHelper;
use App\Common\Service\Language;
use App\Landing\Controller\PsihologProfileController;
use App\Psiholog\Entity\PsihologPrice;
use App\Psiholog\Entity\PsihologProfile;
use App\User\Entity\User;
use App\User\Service\Authenticator\UserAutoLoginAuthenticator;
use App\User\Service\RouteNames;
use App\User\Service\TimeHelper;
use Sendpulse\RestApi\ApiClient;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;
use Symfony\Contracts\Translation\TranslatorInterface;
use Twig\Environment;

class EmailSender
{
    public function __construct(
        private ApiClient $sendpulseClient,
        private Environment $twig,
        private UrlGeneratorInterface $urlGenerator,
        private DateLocalizedHelper $dateLocalizedHelper,
        private TimeHelper $userTimeHelper,
        private TranslatorInterface $translator
    ) {
    }

    public function sendAutologin(User $user, string $autologinLink): bool
    {
        $content = [];
        $content['autologinLink'] = $autologinLink;
        $html = $this->twig->render('@notification/autologin.html.twig', $content);

        $subject = $this->translator->trans('Вход в личный кабинет');

        return $this->sendEmail($user->getEmail(), $subject, $html);
    }

    public function sendConsultationCancelled(
        User $user,
        PsihologProfile $psihologProfile,
        \DateTime $consultationTime,
        bool $notInTimeByPsiholog = false,
        ?PsihologPrice $price = null
    ): bool
    {
        $content = [];
        $bookLink = $this->urlGenerator->generate(\App\Landing\Service\RouteNames::PSIHOLOG_PROFILE, [
            'id' => $psihologProfile->getPsiholog()->getId(),
        ], UrlGeneratorInterface::ABSOLUTE_URL) . '#' . PsihologProfileController::BOOK_CONSULTATION_HTML_ID;
        $content['bookLink'] = $this->createAutologinLink($user, [
            UserAutoLoginAuthenticator::REDIRECT_TO_PARAM_NAME => $bookLink
        ]);
        $content['psihologProfile'] = $psihologProfile;

        $this->userTimeHelper->setTimezone(new \DateTimeZone($user->getTimezone()));
        $consultationTimeUserTz = $this->userTimeHelper->toUserTzDateTimeFromDateTime($consultationTime);

        [
            'time' => $time,
            'dayOfWeekFull' => $dayOfWeekFull,
            'dayOfMonth' => $dayOfMonth,
            'month' => $month,
        ] = $this->dateLocalizedHelper->getDateTimeGoodLookingParts($consultationTimeUserTz, Language::RU);
        $month = mb_strtolower($month);
        $content['consultationDateLabel'] = "{$dayOfMonth} {$month} на {$time}, {$dayOfWeekFull}";

        $content['notInTimeByPsiholog'] = $notInTimeByPsiholog;
        $content['price'] = $price;

        $html = $this->twig->render('@notification/psiholog-cancelled-consultation.html.twig', $content);

        $cancelLabel = $psihologProfile->isWoman() ? 'отменила' : 'отменил';
        $subject = "{$psihologProfile->getFullName()} {$cancelLabel} консультацию {$content['consultationDateLabel']}";

        return $this->sendEmail($user->getEmail(), $subject, $html);
    }

    private function createAutologinLink(User $user, array $params = []): string
    {
        $token = $user->getUserAutologinToken()->getToken();

        return $this->urlGenerator->generate(RouteNames::AUTO_LOGIN, [
            UserAutoLoginAuthenticator::TOKEN_PARAM_NAME => $token,
        ] + $params, UrlGeneratorInterface::ABSOLUTE_URL);
    }

    private function sendEmail(string $email, string $subject, string $content): bool
    {
        $email = [
            'html' => $content,
            'text' => strip_tags($content),
            'subject' => $subject,
            'from' => [
                'name' => 'GoodHelp',
                'email' => 'support@goodhelp.com.ua',
            ],
            'to' => [
                [
                    'email' => $email,
                ],
            ],
        ];

        try {
            $result = $this->sendpulseClient->smtpSendMail($email);

            return !empty($result->result);
        } catch (\Exception $e) {
            //todo: log errors
            return false;
        }
    }
}