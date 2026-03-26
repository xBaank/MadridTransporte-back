using MadridTransporte.Api.Data;
using MadridTransporte.Api.Loader;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;

var builder = Host.CreateApplicationBuilder(args);

builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("Postgres"))
);
builder.Services.AddHttpClient<DataLoader>();

using var host = builder.Build();

using var scope = host.Services.CreateScope();
var loader = scope.ServiceProvider.GetRequiredService<DataLoader>();
await loader.LoadDataAsync();
