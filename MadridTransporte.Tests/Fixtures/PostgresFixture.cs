using System.Text.Json;
using MadridTransporte.Api.Data;
using MadridTransporte.Api.Loader;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc.Testing;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Testcontainers.PostgreSql;
using TUnit.Core.Interfaces;

namespace MadridTransporte.Tests.Fixtures;

public class PostgresFixture : IAsyncInitializer, IAsyncDisposable
{
    public static readonly JsonSerializerOptions JsonOptions = new()
    {
        PropertyNameCaseInsensitive = true,
    };

    private PostgreSqlContainer _container = null!;
    public WebApplicationFactory<Program> Factory { get; private set; } = null!;
    public HttpClient Client { get; private set; } = null!;

    public async Task InitializeAsync()
    {
        _container = new PostgreSqlBuilder("postgres:16-alpine")
            .WithDatabase("madrid_transporte")
            .WithUsername("app")
            .WithPassword("secret")
            .Build();

        await _container.StartAsync();

        Factory = new WebApplicationFactory<Program>().WithWebHostBuilder(builder =>
        {
            builder.UseEnvironment("Testing");

            // Load the API project's user-secrets (Emt/ElCano keys) so external-API tests run
            // locally. Optional, so CI is unaffected and keeps supplying them via env vars.
            builder.ConfigureAppConfiguration(config =>
                config.AddUserSecrets(typeof(Program).Assembly, optional: true)
            );

            builder.ConfigureServices(services =>
            {
                var descriptor = services.SingleOrDefault(d =>
                    d.ServiceType == typeof(DbContextOptions<AppDbContext>)
                );
                if (descriptor is not null)
                    services.Remove(descriptor);

                services.AddDbContext<AppDbContext>(options =>
                    options.UseNpgsql(_container.GetConnectionString())
                );

                services.AddHttpClient<DataLoader>();
            });
        });

        Client = Factory.CreateClient();

        using var scope = Factory.Services.CreateScope();
        var loader = scope.ServiceProvider.GetRequiredService<DataLoader>();
        await loader.LoadDataAsync();
    }

    public async ValueTask DisposeAsync()
    {
        await Factory.DisposeAsync();
        await _container.StopAsync();
        await _container.DisposeAsync();
    }
}
