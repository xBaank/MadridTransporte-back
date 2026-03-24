using System.Text.Json;
using MadridTransporte.Api.Data;
using MadridTransporte.Api.Loader;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc.Testing;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using Testcontainers.PostgreSql;

namespace MadridTransporte.Tests.Fixtures;

public static class PostgresFixture
{
    public static readonly JsonSerializerOptions JsonOptions = new() { PropertyNameCaseInsensitive = true };

    private static readonly SemaphoreSlim Lock = new(1, 1);
    private static bool _initialized;

    private static PostgreSqlContainer _container = null!;
    public static WebApplicationFactory<Program> Factory { get; private set; } = null!;
    public static HttpClient Client { get; private set; } = null!;

    public static async Task EnsureInitialized()
    {
        if (_initialized) return;

        await Lock.WaitAsync();
        try
        {
            if (_initialized) return;

            _container = new PostgreSqlBuilder("postgres:16-alpine")
                .WithDatabase("madrid_transporte")
                .WithUsername("app")
                .WithPassword("secret")
                .Build();

            await _container.StartAsync();

            Factory = new WebApplicationFactory<Program>()
                .WithWebHostBuilder(builder =>
                {
                    builder.UseEnvironment("Testing");
                    builder.ConfigureServices(services =>
                    {
                        var descriptor = services.SingleOrDefault(d =>
                            d.ServiceType == typeof(DbContextOptions<AppDbContext>));
                        if (descriptor != null) services.Remove(descriptor);

                        services.AddDbContext<AppDbContext>(options =>
                            options.UseNpgsql(_container.GetConnectionString()));
                    });
                });

            Client = Factory.CreateClient();

            // Load GTFS data like the Kotlin MongoContainer.start() does
            using var scope = Factory.Services.CreateScope();
            var loader = scope.ServiceProvider.GetRequiredService<DataLoader>();
            await loader.LoadDataAsync();

            _initialized = true;
        }
        finally
        {
            Lock.Release();
        }
    }
}
