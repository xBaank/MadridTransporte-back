using System.Net;
using System.Net.Http.Json;
using MadridTransporte.Api.Dtos;
using MadridTransporte.Tests.Fixtures;
using Shouldly;

namespace MadridTransporte.Tests;

public enum ItinerariesUrls
{
    EmtDirectionBased,
    InterurbanDirectionBased,
    Interurban2DirectionBased,
    UrbanDirectionBased,
    MetroDirectionBased,
    TrainDirectionBased,
    TramDirectionBased,

    EmtCodeBased,
    InterurbanCodeBased,
    UrbanCodeBased,
    MetroCodeBased,
    TrainCodeBased,
    TramCodeBased,
}

public class ItinerariesTests
{
    private static (string url, int direction) GetItineraryTestData(ItinerariesUrls code) => code switch
    {
        ItinerariesUrls.EmtDirectionBased => ("/lines/emt/144/itineraries/1?stopCode=4597", 1),
        ItinerariesUrls.InterurbanDirectionBased => ("/lines/bus/8__450___/itineraries/1?stopCode=08242", 1),
        ItinerariesUrls.Interurban2DirectionBased => ("/lines/bus/8__428___/itineraries/2?stopCode=08242", 2),
        ItinerariesUrls.UrbanDirectionBased => ("/lines/bus/9__2__065_/itineraries/2?stopCode=08242", 2),
        ItinerariesUrls.MetroDirectionBased => ("/lines/metro/4__12___/itineraries/1?stopCode=205", 1),
        ItinerariesUrls.TrainDirectionBased => ("/lines/train/5__4_A__/itineraries/1?stopCode=53", 1),
        ItinerariesUrls.TramDirectionBased => ("/lines/tram/10__ML4___/itineraries/1?stopCode=64", 1),

        ItinerariesUrls.EmtCodeBased => ("/lines/emt/itineraries/144_A", 1),
        ItinerariesUrls.InterurbanCodeBased => ("/lines/bus/itineraries/8__450____1_-_IT_1", 1),
        ItinerariesUrls.UrbanCodeBased => ("/lines/bus/itineraries/9__2__065__2_-_IT_1", 2),
        ItinerariesUrls.MetroCodeBased => ("/lines/metro/itineraries/4__12_2___1__IT_1", 2),
        ItinerariesUrls.TrainCodeBased => ("/lines/train/itineraries/340742", 1),
        ItinerariesUrls.TramCodeBased => ("/lines/tram/itineraries/10__4_1___1__IT_1", 1),
        _ => throw new ArgumentOutOfRangeException(nameof(code)),
    };

    [Test]
    [Arguments(ItinerariesUrls.EmtDirectionBased)]
    [Arguments(ItinerariesUrls.InterurbanDirectionBased)]
    [Arguments(ItinerariesUrls.Interurban2DirectionBased)]
    [Arguments(ItinerariesUrls.UrbanDirectionBased)]
    [Arguments(ItinerariesUrls.MetroDirectionBased)]
    [Arguments(ItinerariesUrls.TrainDirectionBased)]
    [Arguments(ItinerariesUrls.TramDirectionBased)]
    [Arguments(ItinerariesUrls.EmtCodeBased)]
    [Arguments(ItinerariesUrls.InterurbanCodeBased)]
    [Arguments(ItinerariesUrls.UrbanCodeBased)]
    [Arguments(ItinerariesUrls.MetroCodeBased)]
    [Arguments(ItinerariesUrls.TrainCodeBased)]
    [Arguments(ItinerariesUrls.TramCodeBased)]
    public async Task Should_Get_Itineraries_From_Line(ItinerariesUrls code)
    {
        await PostgresFixture.EnsureInitialized();

        var (url, expectedDirection) = GetItineraryTestData(code);
        var response = await PostgresFixture.Client.GetAsync(url);
        response.StatusCode.ShouldBe(HttpStatusCode.OK);

        var itinerary = await response.Content.ReadFromJsonAsync<ItineraryDto>(PostgresFixture.JsonOptions);
        itinerary.ShouldNotBeNull();
        itinerary.CodItinerary.ShouldNotBeNullOrEmpty();
        itinerary.Direction.ShouldBe(expectedDirection);
        itinerary.Stops.ShouldNotBeEmpty();

        foreach (var stop in itinerary.Stops)
        {
            stop.FullStopCode.ShouldNotBeNullOrEmpty();
            stop.Order.ShouldBeGreaterThanOrEqualTo(0);
        }
    }

    [Test]
    public async Task Should_Not_Get_Itineraries_From_Line()
    {
        await PostgresFixture.EnsureInitialized();

        var response = await PostgresFixture.Client.GetAsync("/lines/bus/asd/itineraries/1?stopCode=01231");
        response.StatusCode.ShouldBe(HttpStatusCode.NotFound);
    }
}
