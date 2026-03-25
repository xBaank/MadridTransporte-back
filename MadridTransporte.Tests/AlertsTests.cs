using System.Net;
using System.Net.Http.Json;
using MadridTransporte.Api.Dtos;
using MadridTransporte.Tests.Fixtures;
using Shouldly;

namespace MadridTransporte.Tests;

[ClassDataSource<PostgresFixture>(Shared = SharedType.PerTestSession)]
public class AlertsTests(PostgresFixture fixture)
{
    [Test]
    [Arguments("/stops/bus/alerts")]
    [Arguments("/stops/train/alerts")]
    [Arguments("/stops/metro/alerts")]
    [Arguments("/stops/tram/alerts")]
    [Arguments("/stops/emt/alerts")]
    public async Task Should_Get_Alerts(string url)
    {
        var response = await fixture.Client.GetAsync(url);
        response.StatusCode.ShouldBe(HttpStatusCode.OK);

        var alerts = await response.Content.ReadFromJsonAsync<List<AlertDto>>(PostgresFixture.JsonOptions);
        alerts.ShouldNotBeNull();

        foreach (var alert in alerts)
        {
            alert.CodLine.ShouldNotBeNullOrEmpty();
            alert.Description.ShouldNotBeNullOrEmpty();
            alert.Stops.ShouldNotBeNull();
        }
    }
}
