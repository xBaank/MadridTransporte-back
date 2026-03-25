using System.Net;
using System.Net.Http.Json;
using MadridTransporte.Api.Dtos;
using MadridTransporte.Tests.Fixtures;
using Shouldly;

namespace MadridTransporte.Tests;

[ClassDataSource<PostgresFixture>(Shared = SharedType.PerTestSession)]
public class StopsTests(PostgresFixture fixture)
{
    [Test]
    public async Task Should_Get_All_Stops()
    {
        var response = await fixture.Client.GetAsync("/stops/all");
        response.StatusCode.ShouldBe(HttpStatusCode.OK);

        var stops = await response.Content.ReadFromJsonAsync<List<StopDto>>(PostgresFixture.JsonOptions);
        stops.ShouldNotBeNull();
        stops.ShouldNotBeEmpty();

        foreach (var stop in stops)
        {
            stop.StopCode.ShouldNotBeNullOrEmpty();
            stop.StopName.ShouldNotBeNullOrEmpty();
            stop.CodMode.ShouldBeGreaterThan(0);
            stop.FullStopCode.ShouldNotBeNullOrEmpty();
        }
    }
}
