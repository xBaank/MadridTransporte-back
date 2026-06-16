using System.Net;
using System.Net.Http.Json;
using MadridTransporte.Api.Dtos;
using MadridTransporte.Tests.Fixtures;
using Shouldly;

namespace MadridTransporte.Tests;

[ClassDataSource<PostgresFixture>(Shared = SharedType.PerTestSession)]
public class LocationsTests(PostgresFixture fixture)
{
    // EMT URLs use the bare line code (the GTFS route_id). Line 881 ("S10") used to return
    // HTTP 500 here because the null-route fallback parsed the bare code as a full line code.
    [Test]
    [Arguments("/lines/emt/881/locations/1?stopCode=34")]
    [Arguments("/lines/emt/144/locations/1?stopCode=4597")]
    public async Task Should_Get_Emt_Line_Locations(string url)
    {
        var response = await fixture.Client.GetAsync(url);
        var body = await response.Content.ReadAsStringAsync();
        response.StatusCode.ShouldBe(HttpStatusCode.OK, body);

        var locations = await response.Content.ReadFromJsonAsync<VehicleLocationsDto>(
            PostgresFixture.JsonOptions
        );
        locations.ShouldNotBeNull();
        // EMT mode, regardless of whether the line has a route row in the DB.
        locations.CodMode.ShouldBe(int.Parse(MadridTransporte.Api.Utils.CodeUtils.EmtCodMode));
        locations.LineCode.ShouldNotBeNullOrEmpty();
        // Vehicle positions depend on live service, so the list may be empty — but each entry,
        // when present, must be well-formed.
        foreach (var vehicle in locations.Locations)
        {
            vehicle.SimpleLineCode.ShouldNotBeNullOrEmpty();
            vehicle.Coordinates.ShouldNotBeNull();
        }
    }
}
