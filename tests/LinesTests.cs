using System.Net;
using System.Text.Json;
using MadridTransporte.Api.Data;
using MadridTransporte.Api.Data.Entities;
using MadridTransporte.Tests.Fixtures;
using Microsoft.Extensions.DependencyInjection;
using Shouldly;

namespace MadridTransporte.Tests;

public class LinesTests
{
    [Test]
    public async Task GetAllRoutes_ReturnsEmptyArrayWhenNoData()
    {
        var fixture = new PostgresFixture();
        await fixture.InitializeAsync();

        try
        {
            var response = await fixture.Client.GetAsync("/lines/all");
            var body = await response.Content.ReadAsStringAsync();
            var json = JsonSerializer.Deserialize<JsonElement>(body);

            response.StatusCode.ShouldBe(HttpStatusCode.OK);
            json.ValueKind.ShouldBe(JsonValueKind.Array);
            json.GetArrayLength().ShouldBe(0);
        }
        finally
        {
            await fixture.DisposeAsync();
        }
    }

    [Test]
    public async Task GetAllRoutes_ReturnsSeededRoutes()
    {
        var fixture = new PostgresFixture();
        await fixture.InitializeAsync();

        try
        {
            using (var scope = fixture.Factory.Services.CreateScope())
            {
                var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
                db.Routes.Add(new TransitRoute
                {
                    FullLineCode = "8__001___", SimpleLineCode = "001",
                    RouteName = "Sol - Aluche", CodMode = "8",
                });
                db.Itineraries.Add(new Itinerary
                {
                    ItineraryCode = "ITI001", FullLineCode = "8__001___",
                    Direction = 0, TripId = "T1", ServiceId = "S1", TripName = "Aluche",
                });
                db.Itineraries.Add(new Itinerary
                {
                    ItineraryCode = "ITI002", FullLineCode = "8__001___",
                    Direction = 1, TripId = "T2", ServiceId = "S1", TripName = "Sol",
                });
                await db.SaveChangesAsync();
            }

            var response = await fixture.Client.GetAsync("/lines/all");
            var body = await response.Content.ReadAsStringAsync();
            var json = JsonSerializer.Deserialize<JsonElement>(body);

            response.StatusCode.ShouldBe(HttpStatusCode.OK);
            json.GetArrayLength().ShouldBe(1);

            var route = json.EnumerateArray().First();
            route.GetProperty("fullLineCode").GetString().ShouldBe("8__001___");
            route.GetProperty("simpleLineCode").GetString().ShouldBe("001");
            route.GetProperty("codMode").GetInt32().ShouldBe(8);
            route.GetProperty("routeName").GetString().ShouldBe("Sol - Aluche");
            route.GetProperty("itineraries").GetArrayLength().ShouldBe(2);
        }
        finally
        {
            await fixture.DisposeAsync();
        }
    }

    [Test]
    public async Task GetAllRoutes_ItineraryFieldsAreCorrect()
    {
        var fixture = new PostgresFixture();
        await fixture.InitializeAsync();

        try
        {
            using (var scope = fixture.Factory.Services.CreateScope())
            {
                var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
                db.Routes.Add(new TransitRoute
                {
                    FullLineCode = "4__003___", SimpleLineCode = "003",
                    RouteName = "Moncloa - Villaverde", CodMode = "4",
                });
                db.Itineraries.Add(new Itinerary
                {
                    ItineraryCode = "ITI_M3_1", FullLineCode = "4__003___",
                    Direction = 0, TripId = "TM1", ServiceId = "SM1", TripName = "Villaverde Alto",
                });
                await db.SaveChangesAsync();
            }

            var response = await fixture.Client.GetAsync("/lines/all");
            var body = await response.Content.ReadAsStringAsync();
            var json = JsonSerializer.Deserialize<JsonElement>(body);

            var route = json.EnumerateArray().First();
            var itinerary = route.GetProperty("itineraries").EnumerateArray().First();

            itinerary.GetProperty("itineraryCode").GetString().ShouldBe("ITI_M3_1");
            itinerary.GetProperty("direction").GetInt32().ShouldBe(1); // stored as 0, returned as +1
            itinerary.GetProperty("tripName").GetString().ShouldBe("Villaverde Alto");
            itinerary.GetProperty("serviceId").GetString().ShouldBe("SM1");
        }
        finally
        {
            await fixture.DisposeAsync();
        }
    }

    [Test]
    [Arguments("/lines/bus/shapes/nonexistent")]
    [Arguments("/lines/metro/shapes/nonexistent")]
    [Arguments("/lines/train/shapes/nonexistent")]
    public async Task GetShapes_ReturnsEmptyForNonexistentItinerary(string url)
    {
        var fixture = new PostgresFixture();
        await fixture.InitializeAsync();

        try
        {
            var response = await fixture.Client.GetAsync(url);
            var body = await response.Content.ReadAsStringAsync();
            var json = JsonSerializer.Deserialize<JsonElement>(body);

            response.StatusCode.ShouldBe(HttpStatusCode.OK);
            json.GetArrayLength().ShouldBe(0);
        }
        finally
        {
            await fixture.DisposeAsync();
        }
    }

    [Test]
    public async Task GetShapes_ReturnsSeededShapes()
    {
        var fixture = new PostgresFixture();
        await fixture.InitializeAsync();

        try
        {
            using (var scope = fixture.Factory.Services.CreateScope())
            {
                var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
                db.Shapes.AddRange(
                    new Shape { ItineraryId = "ITI100", Latitude = 40.42, Longitude = -3.70, Sequence = 0, Distance = 0.0 },
                    new Shape { ItineraryId = "ITI100", Latitude = 40.43, Longitude = -3.71, Sequence = 1, Distance = 150.5 },
                    new Shape { ItineraryId = "ITI100", Latitude = 40.44, Longitude = -3.72, Sequence = 2, Distance = 310.0 });
                await db.SaveChangesAsync();
            }

            var response = await fixture.Client.GetAsync("/lines/bus/shapes/ITI100");
            var body = await response.Content.ReadAsStringAsync();
            var json = JsonSerializer.Deserialize<JsonElement>(body);

            response.StatusCode.ShouldBe(HttpStatusCode.OK);
            json.GetArrayLength().ShouldBe(3);

            var first = json.EnumerateArray().First();
            first.GetProperty("latitude").GetDouble().ShouldBe(40.42);
            first.GetProperty("longitude").GetDouble().ShouldBe(-3.70);
            first.GetProperty("sequence").GetInt32().ShouldBe(0);
            first.GetProperty("distance").GetDouble().ShouldBe(0.0);

            var last = json.EnumerateArray().Last();
            last.GetProperty("sequence").GetInt32().ShouldBe(2);
        }
        finally
        {
            await fixture.DisposeAsync();
        }
    }

    [Test]
    public async Task GetShapes_ReturnsOrderedBySequence()
    {
        var fixture = new PostgresFixture();
        await fixture.InitializeAsync();

        try
        {
            using (var scope = fixture.Factory.Services.CreateScope())
            {
                var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
                // Insert out of order
                db.Shapes.AddRange(
                    new Shape { ItineraryId = "ITI200", Latitude = 40.44, Longitude = -3.72, Sequence = 2, Distance = 300.0 },
                    new Shape { ItineraryId = "ITI200", Latitude = 40.42, Longitude = -3.70, Sequence = 0, Distance = 0.0 },
                    new Shape { ItineraryId = "ITI200", Latitude = 40.43, Longitude = -3.71, Sequence = 1, Distance = 150.0 });
                await db.SaveChangesAsync();
            }

            var response = await fixture.Client.GetAsync("/lines/metro/shapes/ITI200");
            var body = await response.Content.ReadAsStringAsync();
            var json = JsonSerializer.Deserialize<JsonElement>(body);

            var shapes = json.EnumerateArray().ToList();
            shapes[0].GetProperty("sequence").GetInt32().ShouldBe(0);
            shapes[1].GetProperty("sequence").GetInt32().ShouldBe(1);
            shapes[2].GetProperty("sequence").GetInt32().ShouldBe(2);
        }
        finally
        {
            await fixture.DisposeAsync();
        }
    }

    [Test]
    public async Task GetItineraryByCode_ReturnsSeededItinerary()
    {
        var fixture = new PostgresFixture();
        await fixture.InitializeAsync();

        try
        {
            using (var scope = fixture.Factory.Services.CreateScope())
            {
                var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
                db.Itineraries.Add(new Itinerary
                {
                    ItineraryCode = "ITI_BUS_1", FullLineCode = "8__050___",
                    Direction = 0, TripId = "TRIP50", ServiceId = "SVC50", TripName = "Carabanchel",
                });
                db.StopOrders.AddRange(
                    new StopOrder { FullStopCode = "8_100", TripId = "TRIP50", Order = 0, DepartureTime = 28800000 },
                    new StopOrder { FullStopCode = "8_101", TripId = "TRIP50", Order = 1, DepartureTime = 29100000 },
                    new StopOrder { FullStopCode = "8_102", TripId = "TRIP50", Order = 2, DepartureTime = 29400000 });
                await db.SaveChangesAsync();
            }

            var response = await fixture.Client.GetAsync("/lines/bus/itineraries/ITI_BUS_1");
            var body = await response.Content.ReadAsStringAsync();
            var json = JsonSerializer.Deserialize<JsonElement>(body);

            response.StatusCode.ShouldBe(HttpStatusCode.OK);
            json.GetProperty("codItinerary").GetString().ShouldBe("ITI_BUS_1");
            json.GetProperty("direction").GetInt32().ShouldBe(1); // stored 0 → returned 1
            json.GetProperty("stops").GetArrayLength().ShouldBe(3);

            var stops = json.GetProperty("stops").EnumerateArray().ToList();
            stops[0].GetProperty("fullStopCode").GetString().ShouldBe("8_100");
            stops[0].GetProperty("order").GetInt32().ShouldBe(0);
            stops[1].GetProperty("fullStopCode").GetString().ShouldBe("8_101");
            stops[2].GetProperty("fullStopCode").GetString().ShouldBe("8_102");
        }
        finally
        {
            await fixture.DisposeAsync();
        }
    }

    [Test]
    public async Task GetItineraryByCode_ReturnsNotFoundForNonexistent()
    {
        var fixture = new PostgresFixture();
        await fixture.InitializeAsync();

        try
        {
            var response = await fixture.Client.GetAsync("/lines/bus/itineraries/NONEXISTENT");
            response.StatusCode.ShouldBe(HttpStatusCode.NotFound);
        }
        finally
        {
            await fixture.DisposeAsync();
        }
    }

    [Test]
    public async Task GetItineraryByLineAndDirection_ReturnsSeededItinerary()
    {
        var fixture = new PostgresFixture();
        await fixture.InitializeAsync();

        try
        {
            using (var scope = fixture.Factory.Services.CreateScope())
            {
                var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
                db.Itineraries.Add(new Itinerary
                {
                    ItineraryCode = "ITI_DIR", FullLineCode = "8__070___",
                    Direction = 0, TripId = "TRIPD1", ServiceId = "SVCD1", TripName = "Arganzuela",
                });
                db.StopOrders.AddRange(
                    new StopOrder { FullStopCode = "8_300", TripId = "TRIPD1", Order = 0, DepartureTime = 30000000 },
                    new StopOrder { FullStopCode = "8_301", TripId = "TRIPD1", Order = 1, DepartureTime = 30300000 });
                await db.SaveChangesAsync();
            }

            // direction=1 in URL maps to direction=0 in DB (direction - 1)
            var response = await fixture.Client.GetAsync("/lines/bus/8__070___/itineraries/1");
            var body = await response.Content.ReadAsStringAsync();
            var json = JsonSerializer.Deserialize<JsonElement>(body);

            response.StatusCode.ShouldBe(HttpStatusCode.OK);
            json.GetProperty("codItinerary").GetString().ShouldBe("ITI_DIR");
            json.GetProperty("direction").GetInt32().ShouldBe(1);
            json.GetProperty("stops").GetArrayLength().ShouldBe(2);
        }
        finally
        {
            await fixture.DisposeAsync();
        }
    }

    [Test]
    public async Task GetItineraryByLineAndDirection_ReturnsNotFoundForWrongDirection()
    {
        var fixture = new PostgresFixture();
        await fixture.InitializeAsync();

        try
        {
            using (var scope = fixture.Factory.Services.CreateScope())
            {
                var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
                db.Itineraries.Add(new Itinerary
                {
                    ItineraryCode = "ITI_DIR2", FullLineCode = "8__080___",
                    Direction = 0, TripId = "TRIPD2", ServiceId = "SVCD2", TripName = "Vallecas",
                });
                await db.SaveChangesAsync();
            }

            // direction=2 in URL maps to direction=1 in DB, which doesn't exist
            var response = await fixture.Client.GetAsync("/lines/bus/8__080___/itineraries/2");
            response.StatusCode.ShouldBe(HttpStatusCode.NotFound);
        }
        finally
        {
            await fixture.DisposeAsync();
        }
    }

    [Test]
    public async Task GetAllRoutes_MultipleRoutesWithDifferentModes()
    {
        var fixture = new PostgresFixture();
        await fixture.InitializeAsync();

        try
        {
            using (var scope = fixture.Factory.Services.CreateScope())
            {
                var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
                db.Routes.AddRange(
                    new TransitRoute { FullLineCode = "8__001___", SimpleLineCode = "001", RouteName = "Bus 1", CodMode = "8" },
                    new TransitRoute { FullLineCode = "4__003___", SimpleLineCode = "003", RouteName = "Metro 3", CodMode = "4" },
                    new TransitRoute { FullLineCode = "5__C3___", SimpleLineCode = "C3", RouteName = "Cercanias C3", CodMode = "5" });
                await db.SaveChangesAsync();
            }

            var response = await fixture.Client.GetAsync("/lines/all");
            var body = await response.Content.ReadAsStringAsync();
            var json = JsonSerializer.Deserialize<JsonElement>(body);

            response.StatusCode.ShouldBe(HttpStatusCode.OK);
            json.GetArrayLength().ShouldBe(3);

            var routes = json.EnumerateArray().ToList();
            routes.ShouldContain(r => r.GetProperty("codMode").GetInt32() == 8);
            routes.ShouldContain(r => r.GetProperty("codMode").GetInt32() == 4);
            routes.ShouldContain(r => r.GetProperty("codMode").GetInt32() == 5);
        }
        finally
        {
            await fixture.DisposeAsync();
        }
    }
}
