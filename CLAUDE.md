# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
dotnet build --configuration Release

# Run tests (all)
dotnet test --configuration Release

# Run a single test class
dotnet test --filter "ClassName" --configuration Release
# Example:
dotnet test --filter "HealthCheckTests" --configuration Release

# Run the API locally
dotnet run --project MadridTransporte.Api

# Add a database migration
dotnet ef migrations add <MigrationName> --project MadridTransporte.Api

# Apply migrations
dotnet ef database update --project MadridTransporte.Api

# Regenerate the CRTM SOAP client
dotnet-svcutil "http://www.citram.es:8080/WSMultimodalInformation/MultimodalInformation.svc?wsdl" --outputDir Generated --namespace "*, MadridTransporte.Api.Clients.Crtm.Generated" --sync
```

## Architecture

**Stack:** .NET 10 / C# 13, ASP.NET Core Minimal APIs, PostgreSQL 16, Entity Framework Core with Npgsql.

**Solution:** Two projects — `MadridTransporte.Api` (the web API) and `MadridTransporte.Tests` (integration tests).

### Request Flow

`Endpoints/` → `Services/` → `Data/AppDbContext` (EF Core) + `Clients/` (external HTTP/SOAP)

- **Endpoints** (`Endpoints/LinesEndpoints.cs`, `StopsEndpoints.cs`) — registers Minimal API routes, grouped by transport mode (`/bus`, `/emt`, `/metro`, `/tram`, `/train`)
- **Services** (`Services/`) — business logic: `StopsService`, `RoutesService`, `ItinerariesService`, `ShapesService`
- **Data** (`Data/AppDbContext.cs`) — EF Core context with entities: `Stop`, `TransitRoute`, `Itinerary`, `StopOrder`, `Calendar`, `Shape`, `StopInfo`
- **Clients** (`Clients/`) — external integrations: `CrtmClient` (SOAP), `EmtClient`, `MetroClient`, `BusClient`, `TrainClient`

### GTFS Data Loading

`Loader/` downloads and parses GTFS feeds (CSV) from multiple agencies and bulk-inserts them via `EFCore.BulkExtensions` in batches of 50k records. Loading is triggered via `POST /load`. Data sources run in parallel; train itineraries and metro repeated stops have special handling.

### Entry Point

`Program.cs` wires up DI (DbContext, HTTP clients, CORS, response compression), registers endpoints, exposes `/health` and `/load`, and auto-migrates the database on startup.

### Testing

Tests are integration tests that use `PostgresFixture` (Testcontainers + `postgres:16-alpine`) which spins up a real database, runs migrations, loads GTFS data via `DataLoader`, and provides an `HttpClient` pointed at the running API. Framework is TUnit + Shouldly.

### Configuration

- `appsettings.json` — PostgreSQL connection string, CRTM endpoint/timeout, EMT credentials
- `appsettings.Development.json` — logging overrides
- Local dev DB: `Host=localhost;Database=madrid_transporte;Username=app;Password=secret` (matches `docker-compose.yml`)

### Deployment

`Dockerfile-api` — multi-stage build, exposes port 8080. CI builds and publishes multi-platform images (`linux/amd64`, `linux/arm64`) to Docker Hub on git tags. Tests run on every push via `.github/workflows/dotnet.yml`.
