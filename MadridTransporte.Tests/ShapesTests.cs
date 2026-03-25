using System.Net;
using System.Net.Http.Json;
using MadridTransporte.Api.Dtos;
using MadridTransporte.Tests.Fixtures;
using Shouldly;

namespace MadridTransporte.Tests;

[ClassDataSource<PostgresFixture>(Shared = SharedType.PerTestSession)]
public class ShapesTests(PostgresFixture fixture)
{
    [Test]
    [Arguments("/lines/emt/shapes/144_A")]
    [Arguments("/lines/emt/shapes/011_B")]
    [Arguments("/lines/bus/shapes/8__450____1_-_IT_1")]
    [Arguments("/lines/bus/shapes/9__2__065__2_-_IT_1")]
    [Arguments("/lines/metro/shapes/4__12_1___1__IT_1")]
    [Arguments("/lines/tram/shapes/10__4_1___1__IT_1")]
    public async Task Should_Get_Shapes(string url)
    {
        var response = await fixture.Client.GetAsync(url);
        response.StatusCode.ShouldBe(HttpStatusCode.OK);

        var shapes = await response.Content.ReadFromJsonAsync<List<ShapeDto>>(PostgresFixture.JsonOptions);
        shapes.ShouldNotBeNull();
        shapes.ShouldNotBeEmpty();

        foreach (var shape in shapes)
        {
            shape.Sequence.ShouldBeGreaterThanOrEqualTo(0);
            shape.Latitude.ShouldNotBe(0);
            shape.Longitude.ShouldNotBe(0);
        }
    }

    [Test]
    public async Task Should_Not_Get_Shapes()
    {
        var response = await fixture.Client.GetAsync("/lines/bus/shapes/asd");
        response.StatusCode.ShouldBe(HttpStatusCode.OK);

        var shapes = await response.Content.ReadFromJsonAsync<List<ShapeDto>>(PostgresFixture.JsonOptions);
        shapes.ShouldNotBeNull();
        shapes.ShouldBeEmpty();
    }
}
