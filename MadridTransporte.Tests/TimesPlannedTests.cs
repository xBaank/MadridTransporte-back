using System.Net;
using System.Net.Http.Json;
using MadridTransporte.Api.Dtos;
using MadridTransporte.Tests.Fixtures;
using Shouldly;

namespace MadridTransporte.Tests;

[ClassDataSource<PostgresFixture>(Shared = SharedType.PerTestSession)]
public class TimesPlannedTests(PostgresFixture fixture)
{
    private const string BusStopCode = "08242";

    [Test]
    [Arguments("/stops/bus/" + BusStopCode + "/planned")]
    public async Task Should_Get_Stop_Times_Planned(string url)
    {
        var response = await fixture.Client.GetAsync(url);
        response.StatusCode.ShouldBe(HttpStatusCode.OK);

        var items = await response.Content.ReadFromJsonAsync<List<PlannedTimeDto>>(PostgresFixture.JsonOptions);
        items.ShouldNotBeNull();

        foreach (var item in items)
        {
            item.Destination.ShouldNotBeNullOrEmpty();
            item.ItineraryCode.ShouldNotBeNullOrEmpty();
            item.Arrives.ShouldNotBeEmpty();
        }
    }

    [Test]
    [Arguments("/stops/bus/asdasd/planned")]
    public async Task Should_Not_Get_Stop_Times_Planned(string url)
    {
        var response = await fixture.Client.GetAsync(url);
        response.StatusCode.ShouldBe(HttpStatusCode.NotFound);
    }
}
