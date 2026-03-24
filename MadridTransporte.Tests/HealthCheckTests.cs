using System.Text.Json;
using MadridTransporte.Tests.Fixtures;
using Shouldly;

namespace MadridTransporte.Tests;

public class HealthCheckTests
{
    [Test]
    public async Task Should_Check_Health()
    {
        await PostgresFixture.EnsureInitialized();

        var response = await PostgresFixture.Client.GetAsync("/health");
        var json = JsonSerializer.Deserialize<JsonElement>(await response.Content.ReadAsStringAsync());

        response.StatusCode.ShouldBe(System.Net.HttpStatusCode.OK);
        json.GetProperty("isRunning").GetBoolean().ShouldBeTrue();
    }
}
