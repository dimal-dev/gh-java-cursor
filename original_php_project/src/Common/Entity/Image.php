<?php

declare(strict_types=1);

namespace App\Common\Entity;

use Doctrine\ORM\Mapping as ORM;

#[ORM\Table(name: 'image')]
#[ORM\Entity]
class Image
{
    public const RESOLUTION_TYPE_ORIGINAL = 1;
    #[ORM\Column(name: 'id', type: 'integer', nullable: false, options: ['unsigned' => true])]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'AUTO')]
    private int $id;
    #[ORM\Column(name: 'parent_id', type: 'integer', nullable: true, options: ['unsigned' => true])]
    private ?int $parentId = null;
    #[ORM\Column(name: 'storage_provider', type: 'integer', length: 255, options: ['unsigned' => true])]
    private int $storageProvider;
    #[ORM\Column(name: 'resolution_type', type: 'integer', length: 255, options: ['unsigned' => true])]
    private int $resolutionType;
    #[ORM\Column(name: 'mimetype', type: 'string', length: 100)]
    private string $mimetype;
    #[ORM\Column(name: 'filepath', type: 'string', length: 2000)]
    private $filepath;
    public function getId(): int
    {
        return $this->id;
    }
    public function getParentId(): ?int
    {
        return $this->parentId;
    }
    public function setParentId(?int $parentId): void
    {
        $this->parentId = $parentId;
    }
    public function getStorageProvider(): int
    {
        return $this->storageProvider;
    }
    public function setStorageProvider(int $storageProvider): void
    {
        $this->storageProvider = $storageProvider;
    }
    public function getResolutionType(): int
    {
        return $this->resolutionType;
    }
    public function setResolutionType(int $resolutionType): void
    {
        $this->resolutionType = $resolutionType;
    }
    public function getMimetype(): string
    {
        return $this->mimetype;
    }
    public function setMimetype(string $mimetype): void
    {
        $this->mimetype = $mimetype;
    }
    public function getFilepath()
    {
        return $this->filepath;
    }
    public function setFilepath($filepath): void
    {
        $this->filepath = $filepath;
    }
}
