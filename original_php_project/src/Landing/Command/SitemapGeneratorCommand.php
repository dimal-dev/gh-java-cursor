<?php

namespace App\Landing\Command;

use App\Landing\Entity\PostInterface;
use App\Landing\Service\RouteNames;
use App\Psiholog\Entity\Psiholog;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Component\Console\Command\Command;
use Symfony\Component\Console\Input\InputInterface;
use Symfony\Component\Console\Output\OutputInterface;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;

class SitemapGeneratorCommand extends Command
{
    private const BASE_URL = 'https://goodhelp.com.ua';

    private const STATIC_ROUTE_LIST = [
        RouteNames::MAIN_PAGE => '1',
        RouteNames::PSIHOLOG_LIST => '0.75',
        RouteNames::REQUEST_PSIHOLOG => '0.75',
        RouteNames::CONSULTATION_CONDITIONS => '0.2',
        RouteNames::ABOUT => '0.5',
        RouteNames::BLOG_POST_LIST => '0.4',
    ];

    public function __construct(
        private UrlGeneratorInterface $urlGenerator,
        private string $projectDir,
        private EntityManagerInterface $em,
    ) {
        parent::__construct("landing:sitemap:generate");
    }

    protected function execute(InputInterface $input, OutputInterface $output): int
    {
        $this->generateTopSitemap();
        $this->generatePagesSitemap();
        $this->generateBlogSitemap();

        return 0;
    }

    private function generatePages(string $locale, array $psihologIdList): string
    {
        $lastMod = date(\DateTime::ATOM);
        $urlList = "";

        $urlTemplate = <<<'URLTEMPLATE'
\n<url>
    <loc>%loc%</loc>
    <lastmod>%lastmod%</lastmod>
    <changefreq>%changefreq%</changefreq>
    <priority>%priority%</priority>
</url>
URLTEMPLATE;
        foreach (self::STATIC_ROUTE_LIST as $route => $priority) {
            $loc = self::BASE_URL . $this->urlGenerator->generate($route, ['_locale' => $locale]);
            $urlList .= strtr($urlTemplate, [
                '%loc%' => $loc,
                '%lastmod%' => $lastMod,
                '%changefreq%' => 'daily',
                '%priority%' => $priority,
            ]);
        }

        foreach ($psihologIdList as $psihologId) {
            $loc = self::BASE_URL . $this->urlGenerator->generate(
                    RouteNames::PSIHOLOG_PROFILE,
                    ['_locale' => $locale, 'id' => $psihologId]
                );
            $urlList .= strtr($urlTemplate, [
                '%loc%' => $loc,
                '%lastmod%' => $lastMod,
                '%changefreq%' => 'daily',
                '%priority%' => $priority,
            ]);
        }

        $pagesXml = <<<"SSM"
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd">
    {$urlList}
</urlset>
SSM;

        return $pagesXml;
    }

    private function generateBlogSitemapUrlSet(string $locale): string
    {
        $postList = $this->getPostList($locale);

        $lastMod = date(\DateTime::ATOM);
        $urlList = "";

        $urlTemplate = <<<"URLTEMPLATE"
\n<url>
    <loc>%loc%</loc>
    <lastmod>%lastmod%</lastmod>
    <changefreq>%changefreq%</changefreq>
    <priority>%priority%</priority>
</url>
URLTEMPLATE;
        foreach ($postList as ['id' => $id, 'slug' => $slug]) {
            $loc = self::BASE_URL . $this->urlGenerator->generate(RouteNames::BLOG_POST_VIEW, [
                        '_locale' => $locale,
                        'postId' => $id,
                        'slug' => $slug,
                    ]
                );
            $urlList .= strtr($urlTemplate, [
                '%loc%' => $loc,
                '%lastmod%' => $lastMod,
                '%changefreq%' => 'daily',
                '%priority%' => '0.3',
            ]);
        }

        $pagesXml = <<<"SSM"
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd">
    {$urlList}
</urlset>
SSM;

        return $pagesXml;
    }

    private function generateTopSitemap(): void
    {
        $sitemapPagesUA = self::BASE_URL . '/sitemap.pages.ua.xml';
        $sitemapPagesRU = self::BASE_URL . '/sitemap.pages.ru.xml';

        $sitemapBlogUA = self::BASE_URL . '/sitemap.blog.ua.xml';
        $sitemapBlogRU = self::BASE_URL . '/sitemap.blog.ru.xml';

        $lastMod = date(\DateTime::ATOM);

        $topSitemap = <<<"SM"
<sitemapindex xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://www.sitemaps.org/schemas/sitemap/0.9" xsi:schemaLocation="http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/siteindex.xsd">
    <sitemap>
        <loc>{$sitemapPagesUA}</loc>
        <lastmod>{$lastMod}</lastmod>
    </sitemap>
    <sitemap>
        <loc>{$sitemapPagesRU}</loc>
        <lastmod>{$lastMod}</lastmod>
    </sitemap>
    <sitemap>
        <loc>{$sitemapBlogUA}</loc>
        <lastmod>{$lastMod}</lastmod>
    </sitemap>
    <sitemap>
        <loc>{$sitemapBlogRU}</loc>
        <lastmod>{$lastMod}</lastmod>
    </sitemap>
</sitemapindex>
SM;
        $topSitemapPath = $this->projectDir . '/public/sitemap.xml';
        file_put_contents($topSitemapPath, $topSitemap);
    }

    private function generatePagesSitemap(): void
    {
        $psihologIdList = $this->getPsihologIdList();
        foreach (['ua', 'ru'] as $locale) {
            $sitemapName = "sitemap.pages.{$locale}.xml";
            $path = $this->projectDir . "/public/$sitemapName";
            $sitemapContent = $this->generatePages($locale, $psihologIdList);

            file_put_contents($path, $sitemapContent);
        }
    }

    private function getPsihologIdList(): array
    {
        $qb = $this->em->getConnection()->createQueryBuilder();
        $qb->select('id');
        $qb->from('psiholog');

        $qb->andWhere('role = :role');
        $qb->setParameter(
            'role',
            Psiholog::ROLE_PSIHOLOG
        );

        $idList = $qb->execute()->fetchFirstColumn();

        return $idList;
    }

    private function getPostList(string $locale): array
    {
        $qb = $this->em->getConnection()->createQueryBuilder();
        $qb->select('id, slug');
        $qb->from("blog_post_{$locale}");

        $qb->andWhere('state = :state');
        $qb->setParameter('state', PostInterface::STATE_POSTED);

        return $qb->execute()->fetchAllAssociative();
    }

    private function generateBlogSitemap(): void
    {
        foreach (['ua', 'ru'] as $locale) {
            $sitemapName = "sitemap.blog.{$locale}.xml";
            $path = $this->projectDir . "/public/$sitemapName";
            $sitemapContent = $this->generateBlogSitemapUrlSet($locale);

            file_put_contents($path, $sitemapContent);
        }
    }
}
