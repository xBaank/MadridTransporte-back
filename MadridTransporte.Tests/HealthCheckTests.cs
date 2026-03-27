using System.Text.Json;
using MadridTransporte.Tests.Fixtures;
using Shouldly;

namespace MadridTransporte.Tests;

[ClassDataSource<PostgresFixture>(Shared = SharedType.PerTestSession)]
public class HealthCheckTests(PostgresFixture fixture)
{
    [Test]
    public async Task Should_Check_Health()
    {
        var response = await fixture.Client.GetAsync("/health");
        var json = JsonSerializer.Deserialize<JsonElement>(
            await response.Content.ReadAsStringAsync()
        );

        response.StatusCode.ShouldBe(System.Net.HttpStatusCode.OK);
        json.GetProperty("isRunning").GetBoolean().ShouldBeTrue();
    }
}
