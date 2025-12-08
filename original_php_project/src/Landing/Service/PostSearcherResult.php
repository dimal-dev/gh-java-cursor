<?php

namespace App\Landing\Service;

class PostSearcherResult
{
    private array $postList;
    private bool $morePostsExist;

    public function __construct(array $postList, bool $morePostsRemain)
    {
        $this->postList = $postList;
        $this->morePostsExist = $morePostsRemain;
    }

    public function getPostList(): array
    {
        return $this->postList;
    }

    public function isMorePostsExist(): bool
    {
        return $this->morePostsExist;
    }


}
