<?php

namespace App\Common\Service;


final class SearchCriteriaCreator
{
    public const DEFAULT_LIMIT = 10;
    public const DEFAULT_PAGE = 1;
    public const MINIMAL_NATURAL_INT = 1;

    private const DEFAULT_SORT_FIELD = 'id';
    private const DEFAULT_SORT_DIRECTION = 'DESC';
    private const ALLOWED_SORT_DIRECTIONS = ['ASC', 'DESC'];

    public function create(array $queryParams): SearchCriteria
    {
        $queryParams['pagination'] = $queryParams['pagination'] ?? [];
        $queryParams['pagination']['perpage'] = $queryParams['pagination']['perpage'] ?? 0;
        $queryParams['pagination']['page'] = $queryParams['pagination']['page'] ?? 0;

        $criteria = new SearchCriteria();
        $criteria->limit = $this->toPositiveInt(
            $queryParams['pagination'],
            'perpage',
            self::DEFAULT_LIMIT
        );
        $criteria->page = $this->toPositiveInt(
            $queryParams['pagination'],
            'page',
            self::DEFAULT_PAGE
        );
        $criteria->offset = $this->calculateOffset($criteria->limit, $criteria->page);
        $criteria->sortField = $queryParams['sort']['field'] ?? self::DEFAULT_SORT_FIELD;
        $criteria->sortDirection = $this->extractSortDirection($queryParams);

        $criteria->generalSearch = $this->extractGeneralSearch($queryParams);

        return $criteria;
    }

    private function calculateOffset(int $limit, int $page): int
    {
        return intval(abs(($page - 1) * ($limit)));
    }

    private function extractSortDirection(array $queryParams): string
    {
        $sortDirection = $queryParams['sort']['sort'] ?? self::DEFAULT_SORT_DIRECTION;
        $sortDirection = strtoupper($sortDirection);

        if (in_array($sortDirection, self::ALLOWED_SORT_DIRECTIONS, true)) {
            return $sortDirection;
        }

        return self::DEFAULT_SORT_DIRECTION;
    }

    private function extractGeneralSearch(array $queryParams): ?string
    {
        $generalSearch = $queryParams['query']['generalSearch'] ?? '';
        $generalSearch = trim((string) $generalSearch);

        return $generalSearch ?: null;
    }

    private function toPositiveInt(array $params, string $name, int $default): int
    {
        if (self::MINIMAL_NATURAL_INT > $params[$name]) {
            return $default;
        }

        return intval($params[$name]);
    }
}