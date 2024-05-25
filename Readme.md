# MadridTransporte-back  [![Java CI with Gradle](https://github.com/xBaank/bus-tracker-back/actions/workflows/gradle.yml/badge.svg)](https://github.com/xBaank/bus-tracker-back/actions/workflows/gradle.yml) 

This is the backend for the MadridTransporte app.

| Image                | Version                                                                                                                                                                  |
|-----------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Api       | [![Docker Image Version (latest by date)](https://img.shields.io/docker/v/xbank/bus_tracker_api)](https://hub.docker.com/repository/docker/xbank/bus_tracker_api/general)             |
| Data loader | [![Docker Image Version (latest by date)](https://img.shields.io/docker/v/xbank/bus_tracker_loader)](https://hub.docker.com/repository/docker/xbank/bus_tracker_loader/general) |

## Features

- Bus locations
- Bus stops times
- Metro stops times
- Emt stops times
- Emt locations
- Renfe stops times
- Stops data
- Stops times notifications

It is written in kotlin using ktor.

## How to deploy

I recommend using docker-compose to deploy the backend.

### Docker-compose

```yaml
version: "3.9"
services:
  mongo:
    image: mongo
    restart: unless-stopped
    environment:
      MONGO_INITDB_ROOT_USERNAME: root
      MONGO_INITDB_ROOT_PASSWORD: example
  api:
    image: xbank/bus_tracker_api:latest
    restart: unless-stopped
    depends_on:
      - mongo
    environment:
      - SERVICE_JSON= #WRITE HERE YOUR SERVICE JSON, NOT THE FILE PATH, You can get it from https://console.firebase.google.com/u/0/project/YOUR_PROJECT/settings/serviceaccounts/adminsdk
      - MONGO_CONNECTION_STRING= #WRITE HERE YOUR MONGO CONNECTION STRING
      - NOTIFICATION_DELAY_TIME_SECONDS=60 #WRITE HERE THE DELAY TIME IN SECONDS FOR THE NOTIFICATION SERVICE, DEFAULT IS 60
      - SOAP_TIMEOUT=45 #DEFAULT is 30
  loader:
    image: xbank/bus_tracker_loader:latest
    restart: unless-stopped
    depends_on:
      - mongo
    environment:
      - MONGO_CONNECTION_STRING= #WRITE HERE YOUR MONGO CONNECTION STRING
    volumes:
      - ./entrypoint.sh:/app/entrypoint.sh
    entrypoint: ["/app/entrypoint.sh"]
  nginx:
    image: nginx
    restart: unless-stopped
    depends_on:
      - api
    ports:
      - "443:443"
    volumes:
      - yourNginxConfFilePath:/etc/nginx/conf.d/default.conf #Example below
      - yourPrivateKeyFilePath:/root/ssl/key.pem #You can generate it using letsencrypt
      - yourFullchainFilePath:/root/ssl/cert.pem #You can generate it using letsencrypt
      - yourNginxCacheFolderPath:/data/nginx/cache #You can use a volume to persist cache
    command: [ "nginx", "-g", "daemon off;" ]
```

### Entrypoint
```sh
#!/bin/bash
while true; do
  # Run your service command
  echo "Running loader..."
  java -jar /app/loader.jar

  # Wait for the specified interval (e.g., 5 minutes)
  sleep 1d
done
```

### Nginx conf example

```nginx
upstream servers {
    server api:8080 fail_timeout=50s max_fails=5;

    keepalive 2;
}

limit_req_zone $binary_remote_addr zone=one:10m rate=10r/s;
proxy_cache_path /data/nginx/cache keys_zone=mycache:10m levels=1:2 inactive=60m max_size=2g; proxy_cache_key "$scheme$request_method$host$request_uri";

server {
    limit_req zone=one burst=20;
    listen                  443 ssl;
    listen                  [::]:443 ssl;
    server_name             api.server.com; #Your api domain
    ssl_certificate         /root/ssl/cert.pem;
    ssl_certificate_key     /root/ssl/key.pem;
    location / {  
        proxy_cache mycache;
        proxy_cache_methods GET HEAD;
        proxy_http_version 1.1;
        proxy_set_header   "Connection" "";
        add_header X-Proxy-Cache $upstream_cache_status;    
        proxy_pass http://servers;
    }
}
```

[Frontend](https://github.com/xBaank/bus-tracker-front)
