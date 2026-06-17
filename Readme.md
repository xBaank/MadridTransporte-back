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
      - ConnectionStrings__Postgres=Host=db;Database=madrid_transporte;Username=app;Password=secret
      - Crtm__TimeoutSeconds=45
      # Secrets — required for live CRTM / EMT / Renfe (Adif) data
      - Crtm__PrivateKey=${CRTM_PRIVATEKEY}
      - Emt__PassKey=${EMT_PASSKEY}
      - Emt__ClientId=${EMT_CLIENTID}
      - ElCano__AccessKey=${ELCANO_ACCESSKEY}
      - ElCano__SecretKey=${ELCANO_SECRETKEY}
      - ElCano__UserId=${ELCANO_USERID}
      - ElCano__UserKey=${ELCANO_USERKEY}

  loader:
    image: xbank/bus_tracker_loader:latest
    restart: unless-stopped
    depends_on:
      - db
    environment:
      - ConnectionStrings__Postgres=Host=db;Database=madrid_transporte;Username=app;Password=secret

volumes:
  db_data:
```

The loader should be run periodically (e.g. once a day) to refresh GTFS data. It exits after completing the load. The API auto-migrates the database on startup.

## Configuration

The API is configured via environment variables (using the standard ASP.NET Core `__` separator for nested keys). All of these can also be set in `appsettings.json`.

| Variable | Required | Description |
|----------|----------|-------------|
| `ConnectionStrings__Postgres` | Yes | PostgreSQL connection string. Used by both the API and the loader. |
| `Crtm__TimeoutSeconds` | No | Timeout (seconds) for the CRTM SOAP client. Defaults to `30`. |
| `Crtm__Endpoint` | No | CRTM SOAP endpoint. Defaults to the public CITRAM endpoint. |
| `Crtm__PrivateKey` | Yes* | CRTM AES private key used to encrypt the connection key (must be 16, 24 or 32 bytes). |
| `Emt__PassKey` | Yes* | EMT Mobility Labs API pass key. |
| `Emt__ClientId` | Yes* | EMT Mobility Labs API client id. |
| `ElCano__AccessKey` | Yes* | Adif (El Cano) API access key. |
| `ElCano__SecretKey` | Yes* | Adif (El Cano) API secret key. |
| `ElCano__UserId` | Yes* | Adif (El Cano) API user id. |
| `ElCano__UserKey` | Yes* | Adif (El Cano) API user key (the `User-Key` header). |
| `ElCano__Client` | No | Adif (El Cano) client identifier. Defaults to `AndroidElcanoApp`. |

\* The API starts without these, but the corresponding endpoints (CRTM bus/metro/tram stop times, locations and itineraries; EMT arrivals/locations; Renfe/Adif train times) throw at runtime until the values are supplied. The loader needs only the connection string.

### Secrets in CI

CI reads the secret values from GitHub Actions secrets (`CRTM_PRIVATEKEY`, `EMT_PASSKEY`, `EMT_CLIENTID`, `ELCANO_ACCESSKEY`, `ELCANO_SECRETKEY`, `ELCANO_USERID`, `ELCANO_USERKEY`) and maps them onto the matching `__` environment variables.

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

### Secrets (local)

The CRTM, EMT and Adif (El Cano) secrets are not committed. In the `Development` environment they are loaded from .NET user-secrets:

```bash
dotnet user-secrets set "Crtm:PrivateKey" "<value>" --project MadridTransporte.Api
dotnet user-secrets set "Emt:PassKey" "<value>" --project MadridTransporte.Api
dotnet user-secrets set "Emt:ClientId" "<value>" --project MadridTransporte.Api
dotnet user-secrets set "ElCano:AccessKey" "<value>" --project MadridTransporte.Api
dotnet user-secrets set "ElCano:SecretKey" "<value>" --project MadridTransporte.Api
dotnet user-secrets set "ElCano:UserId" "<value>" --project MadridTransporte.Api
dotnet user-secrets set "ElCano:UserKey" "<value>" --project MadridTransporte.Api
```

See [Configuration](#configuration) for the full list and the production environment-variable equivalents.

[Frontend](https://github.com/xBaank/MadridTransporte-front)
