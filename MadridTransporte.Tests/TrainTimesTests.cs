using System.Net;
using System.Text.Json;
using MadridTransporte.Tests.Fixtures;
using Shouldly;

namespace MadridTransporte.Tests;

[ClassDataSource<PostgresFixture>(Shared = SharedType.PerTestSession)]
public class TrainTimesTests(PostgresFixture fixture)
{
    private const string OriginStopCode = "41";
    private const string DestinationStopCode = "53";

    [Test]
    public async Task Should_Get_Train_Routed_Times()
    {
        var url = $"/stops/train/times?originStopCode={OriginStopCode}&destinationStopCode={DestinationStopCode}";
        var response = await fixture.Client.GetAsync(url);
        var responseBody = await response.Content.ReadAsStringAsync();
        response.StatusCode.ShouldBe(HttpStatusCode.OK, responseBody);

        var body = JsonSerializer.Deserialize<JsonElement>(responseBody, PostgresFixture.JsonOptions);
        body.ValueKind.ShouldBe(JsonValueKind.Object);

        body.TryGetProperty("actTiempoReal", out var actTiempoReal).ShouldBeTrue();
        actTiempoReal.ValueKind.ShouldBeOneOf(JsonValueKind.True, JsonValueKind.False);

        body.TryGetProperty("peticion", out var peticion).ShouldBeTrue();
        peticion.ValueKind.ShouldBe(JsonValueKind.Object);
        peticion.TryGetProperty("cdgoEstOrigen", out var cdgoEstOrigen).ShouldBeTrue();
        cdgoEstOrigen.ValueKind.ShouldBe(JsonValueKind.String);
        peticion.TryGetProperty("cdgoEstDestino", out var cdgoEstDestino).ShouldBeTrue();
        cdgoEstDestino.ValueKind.ShouldBe(JsonValueKind.String);

        body.TryGetProperty("horario", out var horario).ShouldBeTrue();
        horario.ValueKind.ShouldBe(JsonValueKind.Array);
    }

    [Test]
    public async Task Should_Not_Get_Train_Routed_Times()
    {
        var url = "/stops/train/times?originStopCode=asdasd&destinationStopCode=asdasd";
        var response = await fixture.Client.GetAsync(url);
        response.StatusCode.ShouldBe(HttpStatusCode.NotFound);
    }
}
