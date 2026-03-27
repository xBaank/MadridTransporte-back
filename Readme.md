# MadridTransporte-back [![.NET CI](https://github.com/xBaank/MadridTransporte-back/actions/workflows/dotnet.yml/badge.svg)](https://github.com/xBaank/MadridTransporte-back/actions/workflows/dotnet.yml)

This is the backend for the MadridTransporte app.

| Image        | Version |
|--------------|---------|
| Api          | [![Docker Image Version (latest by date)](https://img.shields.io/docker/v/xbank/bus_tracker_api)](https://hub.docker.com/repository/docker/xbank/bus_tracker_api/general) |
| Data loader  | [![Docker Image Version (latest by date)](https://img.shields.io/docker/v/xbank/bus_tracker_loader)](https://hub.docker.com/repository/docker/xbank/bus_tracker_loader/general) |

## Features

- Bus locations
- Bus stop times
- Metro stop times
- EMT stop times
- EMT locations
- Renfe stop times
- Stops data
- Lines data

It is written in C# using .NET 10 and ASP.NET Core Minimal APIs, with PostgreSQL as the database.

## How to deploy

Recommended approach is Docker Compose.

### Docker Compose

```yaml
services:
  db:
    image: postgres:16-alpine
    restart: unless-stopped
    shm_size: 128mb
    environment:
      POSTGRES_USER: app
      POSTGRES_PASSWORD: secret
      POSTGRES_DB: madrid_transporte
    volumes:
      - db_data:/var/lib/postgresql/data

  api:
    image: xbank/bus_tracker_api:latest
    restart: unless-stopped
    depends_on:
      - db
    ports:
      - "8080:8080"
    environment:
      - ConnectionStrings__DefaultConnection=Host=db;Database=madrid_transporte;Username=app;Password=secret
      - SOAP_TIMEOUT=45

  loader:
    image: xbank/bus_tracker_loader:latest
    restart: unless-stopped
    depends_on:
      - db
    environment:
      - ConnectionStrings__DefaultConnection=Host=db;Database=madrid_transporte;Username=app;Password=secret

volumes:
  db_data:
```

The loader should be run periodically (e.g. once a day) to refresh GTFS data. It exits after completing the load. The API auto-migrates the database on startup.

## Development

### Prerequisites

- .NET 10 SDK
- Docker (for the local PostgreSQL instance)

### Local setup

```bash
# Start the local database
docker compose up -d db

# Run the API
dotnet run --project MadridTransporte.Api

# Load GTFS data (run once, or whenever data needs refreshing)
dotnet run --project MadridTransporte.Loader

# Run tests
dotnet test --configuration Release
```

[Frontend](https://github.com/xBaank/MadridTransporte-front)
