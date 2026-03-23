using MadridTransporte.Api.Clients.Bus;
using MadridTransporte.Api.Clients.Crtm;
using MadridTransporte.Api.Clients.Emt;
using MadridTransporte.Api.Clients.Metro;
using MadridTransporte.Api.Clients.Train;
using MadridTransporte.Api.Data;
using MadridTransporte.Api.Endpoints;
using MadridTransporte.Api.Middleware;
using MadridTransporte.Api.Services;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddOpenApi();
builder.Services.AddMemoryCache();

// Database
builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("Postgres")));

// Services
builder.Services.AddScoped<IStopsService, StopsService>();
builder.Services.AddScoped<IRoutesService, RoutesService>();
builder.Services.AddScoped<IItinerariesService, ItinerariesService>();
builder.Services.AddScoped<IShapesService, ShapesService>();

// HTTP Clients
builder.Services.AddHttpClient<ICrtmClient, CrtmClient>();
builder.Services.AddHttpClient<IEmtClient, EmtClient>();
builder.Services.AddHttpClient<IMetroClient, MetroClient>();
builder.Services.AddHttpClient<IBusClient, BusClient>();
builder.Services.AddScoped<ICrtmFallbackClient>(sp => (ICrtmFallbackClient)sp.GetRequiredService<IBusClient>());

builder.Services.AddTransient<ElCanoAuthHandler>();
builder.Services.AddHttpClient("ElCano")
    .ConfigurePrimaryHttpMessageHandler(() => new HttpClientHandler
    {
        ServerCertificateCustomValidationCallback = (_, _, _, _) => true,
    })
    .AddHttpMessageHandler<ElCanoAuthHandler>();
builder.Services.AddHttpClient("Renfe");
builder.Services.AddScoped<ITrainClient, TrainClient>();

// CORS
builder.Services.AddCors(options =>
    options.AddDefaultPolicy(policy =>
        policy.AllowAnyOrigin().AllowAnyMethod().AllowAnyHeader()));

// Compression
builder.Services.AddResponseCompression(options => options.EnableForHttps = true);

var app = builder.Build();

// Auto-migrate database
using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
    await db.Database.MigrateAsync();
}

app.UseMiddleware<ExceptionHandlingMiddleware>();
app.UseCors();
app.UseResponseCompression();

if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}

app.MapGet("/health", () => Results.Ok(new { isRunning = true }));
app.MapStopsEndpoints();
app.MapLinesEndpoints();

app.Run();

public partial class Program;
