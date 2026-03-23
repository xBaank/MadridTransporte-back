using System.Net;
using System.Text.Json;
using MadridTransporte.Api.Data;
using MadridTransporte.Api.Data.Entities;
using MadridTransporte.Tests.Fixtures;
using Microsoft.Extensions.DependencyInjection;
using Shouldly;

namespace MadridTransporte.Tests;

public class StopsTests
{
    [Test]
    public async Task GetAllStops_ReturnsEmptyArrayWhenNoData()
    {
        var fixture = new PostgresFixture();
        await fixture.InitializeAsync();

        try
        {
            var response = await fixture.Client.GetAsync("/stops/all");
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
    public async Task GetAllStops_ReturnsSeededStops()
    {
        var fixture = new PostgresFixture();
        await fixture.InitializeAsync();

        try
        {
            using (var scope = fixture.Factory.Services.CreateScope())
            {
                var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
                db.Stops.AddRange(
                    new Stop
                    {
                        StopCode = "1234", StopName = "Sol", StopLat = 40.4168, StopLon = -3.7038,
                        CodMode = 8, FullStopCode = "8_1234", Wheelchair = 1, Zone = "A",
                    },
                    new Stop
                    {
                        StopCode = "5678", StopName = "Atocha", StopLat = 40.4065, StopLon = -3.6930,
                        CodMode = 4, FullStopCode = "4_5678", Wheelchair = 0, Zone = "A",
                    });
                await db.SaveChangesAsync();
            }

            var response = await fixture.Client.GetAsync("/stops/all");
            var body = await response.Content.ReadAsStringAsync();
            var json = JsonSerializer.Deserialize<JsonElement>(body);

            response.StatusCode.ShouldBe(HttpStatusCode.OK);
            json.GetArrayLength().ShouldBe(2);

            var stops = json.EnumerateArray().ToList();
            stops.ShouldContain(s => s.GetProperty("stopCode").GetString() == "1234");
            stops.ShouldContain(s => s.GetProperty("stopCode").GetString() == "5678");
        }
        finally
        {
            await fixture.DisposeAsync();
        }
    }

    [Test]
    public async Task GetAllStops_ReturnsCorrectFields()
    {
        var fixture = new PostgresFixture();
        await fixture.InitializeAsync();

        try
        {
            using (var scope = fixture.Factory.Services.CreateScope())
            {
                var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
                db.Stops.Add(new Stop
                {
                    StopCode = "1234", StopName = "Sol", StopLat = 40.4168, StopLon = -3.7038,
                    CodMode = 8, FullStopCode = "8_1234", Wheelchair = 1, Zone = "A",
                });
                await db.SaveChangesAsync();
            }

            var response = await fixture.Client.GetAsync("/stops/all");
            var body = await response.Content.ReadAsStringAsync();
            var json = JsonSerializer.Deserialize<JsonElement>(body);
            var stop = json.EnumerateArray().First();

            stop.GetProperty("stopCode").GetString().ShouldBe("1234");
            stop.GetProperty("stopName").GetString().ShouldBe("Sol");
            stop.GetProperty("stopLat").GetDouble().ShouldBe(40.4168);
            stop.GetProperty("stopLon").GetDouble().ShouldBe(-3.7038);
            stop.GetProperty("codMode").GetInt32().ShouldBe(8);
            stop.GetProperty("fullStopCode").GetString().ShouldBe("8_1234");
            stop.GetProperty("wheelchair").GetInt32().ShouldBe(1);
            stop.GetProperty("zone").GetString().ShouldBe("A");
        }
        finally
        {
            await fixture.DisposeAsync();
        }
    }

    [Test]
    [Arguments("/stops/bus/9999/times")]
    [Arguments("/stops/emt/9999/times")]
    [Arguments("/stops/metro/9999/times")]
    [Arguments("/stops/tram/9999/times")]
    [Arguments("/stops/train/9999/times")]
    public async Task GetStopTimes_ReturnsNotFoundForInvalidStop(string url)
    {
        var fixture = new PostgresFixture();
        await fixture.InitializeAsync();

        try
        {
            var response = await fixture.Client.GetAsync(url);
            response.StatusCode.ShouldBe(HttpStatusCode.NotFound);
        }
        finally
        {
            await fixture.DisposeAsync();
        }
    }

    [Test]
    [Arguments("/stops/bus/9999/planned")]
    [Arguments("/stops/emt/9999/planned")]
    [Arguments("/stops/metro/9999/planned")]
    [Arguments("/stops/tram/9999/planned")]
    [Arguments("/stops/train/9999/planned")]
    public async Task GetStopTimesPlanned_ReturnsNotFoundForInvalidStop(string url)
    {
        var fixture = new PostgresFixture();
        await fixture.InitializeAsync();

        try
        {
            var response = await fixture.Client.GetAsync(url);
            response.StatusCode.ShouldBe(HttpStatusCode.NotFound);
        }
        finally
        {
            await fixture.DisposeAsync();
        }
    }

    [Test]
    [Arguments("bus", "8")]
    [Arguments("emt", "6")]
    [Arguments("metro", "4")]
    [Arguments("tram", "10")]
    [Arguments("train", "5")]
    public async Task GetStopTimesPlanned_ReturnsEmptyArrayForStopWithNoSchedule(string mode, string codMode)
    {
        var fixture = new PostgresFixture();
        await fixture.InitializeAsync();

        try
        {
            using (var scope = fixture.Factory.Services.CreateScope())
            {
                var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
                db.Stops.Add(new Stop
                {
                    StopCode = "100", StopName = "Test Stop", StopLat = 40.0, StopLon = -3.7,
                    CodMode = int.Parse(codMode), FullStopCode = $"{codMode}_100", Wheelchair = 0, Zone = "A",
                });
                await db.SaveChangesAsync();
            }

            var response = await fixture.Client.GetAsync($"/stops/{mode}/100/planned");
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
    public async Task GetStopTimesPlanned_ReturnsPlannedTimesWithSeededData()
    {
        var fixture = new PostgresFixture();
        await fixture.InitializeAsync();

        try
        {
            using (var scope = fixture.Factory.Services.CreateScope())
            {
                var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();

                db.Stops.Add(new Stop
                {
                    StopCode = "200", StopName = "Gran Via", StopLat = 40.42, StopLon = -3.70,
                    CodMode = 8, FullStopCode = "8_200", Wheelchair = 1, Zone = "A",
                });

                db.Routes.Add(new TransitRoute
                {
                    FullLineCode = "8__001___", SimpleLineCode = "001", RouteName = "Line 1", CodMode = "8",
                });

                var now = DateTimeOffset.UtcNow;
                var startOfDay = now.Date;
                var futureMs = (long)(now - startOfDay).TotalMilliseconds + 3_600_000; // 1h from now

                db.Calendars.Add(new Calendar
                {
                    ServiceId = "SVC1",
                    Monday = true, Tuesday = true, Wednesday = true,
                    Thursday = true, Friday = true, Saturday = true, Sunday = true,
                    StartDate = now.AddDays(-1).ToUnixTimeMilliseconds(),
                    EndDate = now.AddDays(1).ToUnixTimeMilliseconds(),
                });

                db.Itineraries.Add(new Itinerary
                {
                    ItineraryCode = "ITI001", FullLineCode = "8__001___",
                    Direction = 0, TripId = "TRIP1", ServiceId = "SVC1", TripName = "Aluche",
                });

                db.StopOrders.Add(new StopOrder
                {
                    FullStopCode = "8_200", TripId = "TRIP1", Order = 0, DepartureTime = futureMs,
                });

                await db.SaveChangesAsync();
            }

            var response = await fixture.Client.GetAsync("/stops/bus/200/planned");
            var body = await response.Content.ReadAsStringAsync();
            var json = JsonSerializer.Deserialize<JsonElement>(body);

            response.StatusCode.ShouldBe(HttpStatusCode.OK);
            json.ValueKind.ShouldBe(JsonValueKind.Array);
            json.GetArrayLength().ShouldBeGreaterThan(0);

            var planned = json.EnumerateArray().First();
            planned.GetProperty("fullLineCode").GetString().ShouldBe("8__001___");
            planned.GetProperty("lineCode").GetString().ShouldBe("001");
            planned.GetProperty("destination").GetString().ShouldBe("Aluche");
            planned.GetProperty("codMode").GetInt32().ShouldBe(8);
            planned.GetProperty("direction").GetInt32().ShouldBe(1);
            planned.GetProperty("itineraryCode").GetString().ShouldBe("ITI001");
            planned.GetProperty("arrives").GetArrayLength().ShouldBeGreaterThan(0);
        }
        finally
        {
            await fixture.DisposeAsync();
        }
    }
}
