<?php

declare(strict_types=1);

use Rector\CodeQuality\Rector\Class_\InlineConstructorDefaultToPropertyRector;
use Rector\Config\RectorConfig;
use Rector\Doctrine\Set\DoctrineSetList;
use Rector\Set\ValueObject\LevelSetList;

return static function (RectorConfig $rectorConfig): void {
    $rectorConfig->paths([
        __DIR__ . '/src'
    ]);

    // register a single rule
    $rectorConfig->rule(InlineConstructorDefaultToPropertyRector::class);

    $rectorConfig->sets([
        DoctrineSetList::ANNOTATIONS_TO_ATTRIBUTES,
//            SymfonySetList::ANNOTATIONS_TO_ATTRIBUTES,
//            NetteSetList::ANNOTATIONS_TO_ATTRIBUTES,
//            SensiolabsSetList::FRAMEWORK_EXTRA_61,
    ]);
    // define sets of rules
    //    $rectorConfig->sets([
    //        LevelSetList::UP_TO_PHP_80
    //    ]);
};
