<?php

declare(strict_types=1);

namespace App\Landing\Entity;

use Doctrine\ORM\Mapping as ORM;

#[ORM\Table(name: 'blog_post_ua')]
#[ORM\Entity]
class PostUa implements PostInterface
{
    #[ORM\Column(name: 'id', type: 'integer', nullable: false, options: ['unsigned' => true])]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'AUTO')]
    private int $id;

    #[ORM\Column(name: 'psiholog_id', type: 'integer', nullable: false)]
    private int $psihologId;

    #[ORM\Column(name: 'header', type: 'string', nullable: false, length: 1000)]
    private string $header = '';

    #[ORM\Column(name: 'preview', type: 'text', nullable: false)]
    private string $preview = '';

    #[ORM\Column(name: 'body', type: 'text', nullable: false)]
    private string $body = '';

    #[ORM\Column(name: 'main_image_id', type: 'integer', nullable: true)]
    private ?int $mainImageId = null;

    #[ORM\Column(name: 'posted_at', type: 'datetime', nullable: false)]
    private \DateTime $postedAt;

    #[ORM\Column(name: 'state', type: 'integer')]
    private int $state = self::STATE_DRAFT;

    public function getId(): int
    {
        return $this->id;
    }
    public function getPsihologId(): int
    {
        return $this->psihologId;
    }
    public function setPsihologId(int $psihologId): void
    {
        $this->psihologId = $psihologId;
    }
    public function getHeader(): string
    {
        return $this->header;
    }
    public function setHeader(string $header): void
    {
        $this->header = $header;
    }
    public function getPreview(): string
    {
        return $this->preview;
    }
    public function setPreview(string $preview): void
    {
        $this->preview = $preview;
    }
    public function getBody(): string
    {
        return $this->body;
    }
    public function setBody(string $body): void
    {
        $this->body = $body;
    }
    public function getMainImageId(): ?int
    {
        return $this->mainImageId;
    }
    public function setMainImageId(?int $mainImageId): void
    {
        $this->mainImageId = $mainImageId;
    }
    public function getPostedAt(): \DateTime
    {
        return $this->postedAt;
    }
    public function setPostedAt(\DateTime $postedAt): void
    {
        $this->postedAt = $postedAt;
    }
    public function getState(): int
    {
        return $this->state;
    }
    public function setState(int $state): void
    {
        $this->state = $state;
    }
}
