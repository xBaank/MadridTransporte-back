using System.Net;
using System.Text.Json;
using MadridTransporte.Tests.Fixtures;
using Shouldly;

namespace MadridTransporte.Tests;

public class HealthCheckTests
{
    [Test]
    public async Task HealthEndpoint_ReturnsOk()
    {
        var fixture = new PostgresFixture();
        await fixture.InitializeAsync();

        try
        {
            var response = await fixture.Client.GetAsync("/health");
            var body = await response.Content.ReadAsStringAsync();
            var json = JsonSerializer.Deserialize<JsonElement>(body);

            response.StatusCode.ShouldBe(HttpStatusCode.OK);
            json.GetProperty("isRunning").GetBoolean().ShouldBeTrue();
        }
        finally
        {
            await fixture.DisposeAsync();
        }
    }
}
