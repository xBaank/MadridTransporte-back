using System.Net;
using System.Text.Json;
using MadridTransporte.Tests.Fixtures;
using Shouldly;

namespace MadridTransporte.Tests;

public class AlertsTests
{
    [Test]
    [Arguments("/stops/bus/alerts")]
    [Arguments("/stops/emt/alerts")]
    [Arguments("/stops/metro/alerts")]
    [Arguments("/stops/tram/alerts")]
    [Arguments("/stops/train/alerts")]
    public async Task GetAlerts_ReturnsArrayOrError(string url)
    {
        var fixture = new PostgresFixture();
        await fixture.InitializeAsync();

        try
        {
            var response = await fixture.Client.GetAsync(url);

            // The CRTM SOAP service may not be reachable in tests,
            // so we accept both OK (with data) and OK (empty array)
            response.StatusCode.ShouldBe(HttpStatusCode.OK);
            var body = await response.Content.ReadAsStringAsync();
            var json = JsonSerializer.Deserialize<JsonElement>(body);
            json.ValueKind.ShouldBe(JsonValueKind.Array);
        }
        finally
        {
            await fixture.DisposeAsync();
        }
    }
}
