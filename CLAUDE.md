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

# Run the data loader (loads GTFS data into the database)
dotnet run --project MadridTransporte.Loader

# Add a database migration
dotnet ef migrations add <MigrationName> --project MadridTransporte.Api

# Apply migrations
dotnet ef database update --project MadridTransporte.Api

# Format (CSharpier also runs automatically on every build via CSharpier.MsBuild)
dotnet csharpier .

# Regenerate the CRTM SOAP client
dotnet-svcutil "http://www.citram.es:8080/WSMultimodalInformation/MultimodalInformation.svc?wsdl" --outputDir Generated --namespace "*, MadridTransporte.Api.Clients.Crtm.Generated" --sync
```

## Architecture

**Stack:** .NET 10 / C# 13, ASP.NET Core Minimal APIs, PostgreSQL 16, Entity Framework Core with Npgsql.

NuGet versions are managed centrally in `Directory.Packages.props` (Central Package Management) — add `<PackageReference Include="..." />` without a `Version` and pin the version in `Directory.Packages.props`. Code style is enforced by CSharpier, which formats on build. Tests run on the Microsoft.Testing.Platform runner (configured in `global.json`).

**Solution:** Three projects — `MadridTransporte.Api` (the web API), `MadridTransporte.Loader` (console app for loading GTFS data), and `MadridTransporte.Tests` (integration tests). The Loader references `MadridTransporte.Api` to share `AppDbContext` and the `Loader/` classes.

### Request Flow

`Endpoints/` → `Services/` → `Data/AppDbContext` (EF Core) + `Clients/` (external HTTP/SOAP)

- **Endpoints** (`Endpoints/LinesEndpoints.cs`, `StopsEndpoints.cs`) — registers Minimal API routes, grouped by transport mode (`/bus`, `/emt`, `/metro`, `/tram`, `/train`)
- **Services** (`Services/`) — business logic: `StopsService`, `RoutesService`, `ItinerariesService`, `ShapesService`
- **Data** (`Data/AppDbContext.cs`) — EF Core context with entities: `Stop`, `TransitRoute`, `Itinerary`, `StopOrder`, `Calendar`, `Shape`, `StopInfo`
- **Clients** (`Clients/`) — external integrations grouped by agency subfolder: `CrtmClient` (SOAP; `CrtmAuthHelper` AES-encrypts the connection key with `Crtm:PrivateKey`), `EmtClient`, `MetroClient`, `BusClient`, `TrainClient` (uses two named HTTP clients: `ElCano` with custom SSL bypass via `ElCanoAuthHandler`, and `Renfe`)
- **Error handling** — `Middleware/ExceptionHandlingMiddleware` catches `ApiException` (`Exceptions/ApiException.cs`) and maps it to the HTTP response; it is registered first in the pipeline

### GTFS Data Loading

`MadridTransporte.Api/Loader/` contains `DataLoader`, `GtfsParsers`, and `DataSourceConfig`. It downloads and parses GTFS feeds (CSV) from multiple agencies and bulk-inserts them via `EFCore.BulkExtensions` in batches of 50k records. Data sources run in parallel; train itineraries and metro repeated stops have special handling. The `MadridTransporte.Loader` console app is the entry point for running the load process.

### Entry Point

`Program.cs` wires up DI (DbContext, HTTP clients, CORS, response compression, memory cache), registers endpoints, exposes `/health`, and auto-migrates the database on startup. OpenAPI/Swagger UI is available at `/openapi/v1.json` and `/swagger` in Development.

### Testing

Tests are integration tests that use `PostgresFixture` (Testcontainers + `postgres:16-alpine`) which spins up a real database, runs migrations, loads GTFS data via `DataLoader`, and provides an `HttpClient` pointed at the running API. Framework is TUnit + Shouldly.

### Configuration

- `appsettings.json` — PostgreSQL connection string, CRTM endpoint/timeout, plus **empty placeholders** for the secret values: `Crtm:PrivateKey`, the `Emt` section (`PassKey`, `ClientId`) and the `ElCano` section (`AccessKey`, `SecretKey`, `UserId`, `UserKey`, `Client`)
- Local dev DB: `Host=localhost;Database=madrid_transporte;Username=app;Password=secret` (matches `docker-compose.yml`)

#### Secrets

Real values for the `Crtm:PrivateKey`, `Emt` and `ElCano` secrets are **not committed**. Supply them via:

- **Local dev:** .NET user-secrets (auto-loaded in the `Development` environment). The API project has a `UserSecretsId`. Set values with, e.g.:
  ```bash
  dotnet user-secrets set "Crtm:PrivateKey" "<value>" --project MadridTransporte.Api
  dotnet user-secrets set "Emt:PassKey" "<value>" --project MadridTransporte.Api
  dotnet user-secrets set "Emt:ClientId" "<value>" --project MadridTransporte.Api
  dotnet user-secrets set "ElCano:AccessKey" "<value>" --project MadridTransporte.Api
  dotnet user-secrets set "ElCano:SecretKey" "<value>" --project MadridTransporte.Api
  dotnet user-secrets set "ElCano:UserId" "<value>" --project MadridTransporte.Api
  dotnet user-secrets set "ElCano:UserKey" "<value>" --project MadridTransporte.Api
  ```
- **Production/CI:** environment variables (double-underscore separator), e.g. `Crtm__PrivateKey`, `Emt__PassKey`, `Emt__ClientId`, `ElCano__AccessKey`, `ElCano__SecretKey`, `ElCano__UserId`, `ElCano__UserKey`. CI maps GitHub Actions secrets onto these.

`CrtmAuthHelper`, `EmtClient` and `ElCanoAuthHandler` read these via `IConfiguration` and throw `InvalidOperationException` at runtime if a required key is missing/empty (`IConfiguration.GetRequired` in `Utils/ConfigurationExtensions.cs`). The API starts without them, but the corresponding endpoints throw until the values are supplied; the loader needs only the connection string.

### Deployment

`Dockerfile-api` and `Dockerfile-loader` — multi-stage builds, API exposes port 8080. CI builds and publishes multi-platform images (`linux/amd64`, `linux/arm64`) to Docker Hub on git tags. Tests run on every push via `.github/workflows/dotnet.yml`.
