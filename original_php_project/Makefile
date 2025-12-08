build: up ps composer migrations
prod: up-prod ps composer migrations

up:
	docker network create --driver bridge goodhelp-network || true
	mkdir -p .infrastructure/.redis
	docker-compose -f .infrastructure/docker-compose/docker-compose.yml -f .infrastructure/docker-compose/docker-compose.dev.yml -f .infrastructure/docker-compose/docker-compose.common.yml up -d --build
up-prod:
	docker network create --driver bridge goodhelp-network || true
	mkdir -p .infrastructure/.redis
	docker-compose -f .infrastructure/docker-compose/docker-compose.yml -f .infrastructure/docker-compose/docker-compose.prod.yml -f .infrastructure/docker-compose/docker-compose.common.yml up -d --build

down:
	docker-compose -f .infrastructure/docker-compose/docker-compose.yml -f .infrastructure/docker-compose/docker-compose.dev.yml -f .infrastructure/docker-compose/docker-compose.common.yml stop

down-prod:
	docker-compose -f .infrastructure/docker-compose/docker-compose.yml -f .infrastructure/docker-compose/docker-compose.prod.yml -f .infrastructure/docker-compose/docker-compose.common.yml stop

ps:
	docker-compose -f .infrastructure/docker-compose/docker-compose.yml -f .infrastructure/docker-compose/docker-compose.dev.yml -f .infrastructure/docker-compose/docker-compose.common.yml ps

composer:
	docker exec -t goodhelp-php-fpm bash -c 'COMPOSER_MEMORY_LIMIT=-1 composer install'

migrations:
	docker exec -i goodhelp-php-fpm sh -c 'bin/console doctrine:migrations:migrate -n'

assets:
	docker exec -i goodhelp-php-fpm sh -c 'bin/console assets:install --symlink public/userspace/assets -vv'