using System.Net;
using System.Net.Http.Json;
using MadridTransporte.Api.Dtos;
using MadridTransporte.Tests.Fixtures;
using Shouldly;

namespace MadridTransporte.Tests;

public class RoutesTests
{
    [Test]
    public async Task Should_Get_All_Routes()
    {
        await PostgresFixture.EnsureInitialized();

        var response = await PostgresFixture.Client.GetAsync("/lines/all");
        response.StatusCode.ShouldBe(HttpStatusCode.OK);

        var routes = await response.Content.ReadFromJsonAsync<List<RouteDto>>(PostgresFixture.JsonOptions);
        routes.ShouldNotBeNull();
        routes.ShouldNotBeEmpty();

        foreach (var route in routes)
        {
            route.FullLineCode.ShouldNotBeNullOrEmpty();
            route.SimpleLineCode.ShouldNotBeNullOrEmpty();
            route.RouteName.ShouldNotBeNullOrEmpty();

            foreach (var itinerary in route.Itineraries)
            {
                itinerary.ItineraryCode.ShouldNotBeNullOrEmpty();
                itinerary.TripName.ShouldNotBeNullOrEmpty();
                itinerary.ServiceId.ShouldNotBeNullOrEmpty();
            }
        }
    }
}
