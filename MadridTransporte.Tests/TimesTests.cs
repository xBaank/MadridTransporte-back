using System.Net;
using System.Text.Json;
using MadridTransporte.Api.Dtos;
using MadridTransporte.Tests.Fixtures;
using Shouldly;

namespace MadridTransporte.Tests;

public class TimesTests
{
    private const string BusStopCode = "08242";
    private const string TrainStopCode = "34";
    private const string MetroStopCode = "235";
    private const string EmtStopCode = "73";
    private const string TramStopCode = "58";

    [Test]
    [Arguments("/stops/bus/" + BusStopCode + "/times")]
    [Arguments("/stops/train/" + TrainStopCode + "/times")]
    [Arguments("/stops/metro/" + MetroStopCode + "/times")]
    [Arguments("/stops/emt/" + EmtStopCode + "/times")]
    [Arguments("/stops/tram/" + TramStopCode + "/times")]
    public async Task Should_Get_Stop_Times(string url)
    {
        await PostgresFixture.EnsureInitialized();

        var response = await PostgresFixture.Client.GetAsync(url);
        var responseBody = await response.Content.ReadAsStringAsync();
        response.StatusCode.ShouldBe(HttpStatusCode.OK, responseBody);

        var body = JsonSerializer.Deserialize<StopTimesDto>(responseBody, PostgresFixture.JsonOptions);
        body.ShouldNotBeNull();
        body.CodMode.ShouldBeGreaterThan(0);
        body.StopName.ShouldNotBeNullOrEmpty();
        body.SimpleStopCode.ShouldNotBeNullOrEmpty();
        body.Coordinates.ShouldNotBeNull();
        body.Arrives.ShouldNotBeNull();

        foreach (var arrive in body.Arrives!)
        {
            arrive.CodMode.ShouldBeGreaterThan(0);
            arrive.Line.ShouldNotBeNullOrEmpty();
            arrive.Destination.ShouldNotBeNullOrEmpty();
            arrive.EstimatedArrives.ShouldNotBeEmpty();
        }

        foreach (var incident in body.Incidents)
        {
            incident.Title.ShouldNotBeNullOrEmpty();
            incident.Description.ShouldNotBeNullOrEmpty();
        }
    }

    [Test]
    [Arguments("/stops/bus/asdasd/times")]
    [Arguments("/stops/train/asdasd/times")]
    [Arguments("/stops/metro/asdasd/times")]
    [Arguments("/stops/emt/asdasd/times")]
    [Arguments("/stops/tram/asdasd/times")]
    public async Task Should_Not_Get_Stop_Times(string url)
    {
        await PostgresFixture.EnsureInitialized();

        var response = await PostgresFixture.Client.GetAsync(url);
        response.StatusCode.ShouldBe(HttpStatusCode.NotFound);
    }
}
