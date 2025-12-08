FROM php:8.0.10-cli

WORKDIR "/application"

# Fix debconf warnings upon .build
ARG DEBIAN_FRONTEND=noninteractive

RUN apt-get update
RUN apt-get install -y wget git unzip libicu-dev libzip-dev zlib1g-dev zip cron librabbitmq-dev libssh-dev libpng-dev libjpeg-dev libfreetype6-dev libjpeg62-turbo-dev

ADD https://github.com/mlocati/docker-php-extension-installer/releases/latest/download/install-php-extensions /usr/local/bin/

RUN chmod +x /usr/local/bin/install-php-extensions && sync && \
    install-php-extensions amqp intl zip mysqli pdo_mysql opcache bcmath sockets gd redis

# Install Brotli extension
RUN git clone --recursive --depth=1 https://github.com/kjdev/php-ext-brotli.git /usr/src/php/ext/brotli \
    && docker-php-ext-install brotli

# Install composer
RUN curl -sS https://getcomposer.org/installer | php -- \
        --filename=composer \
        --install-dir=/usr/local/bin && \
        echo "alias composer='COMPOSER_MEMORY_LIMIT=-1 composer'" >> /root/.bashrc && \
        composer

COPY php.ini "$PHP_INI_DIR"/conf.d

CMD cron -f