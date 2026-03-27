using System.Text.Json.Serialization;

namespace MadridTransporte.Api.Dtos;

public class StopTimesDto
{
    public int CodMode { get; set; }
    public required string StopName { get; set; }
    public required string SimpleStopCode { get; set; }
    public string StopCode => $"{CodMode}_{SimpleStopCode}";
    public required CoordinatesDto Coordinates { get; set; }
    public List<ArriveGroupDto>? Arrives { get; set; }
    public required List<IncidentDto> Incidents { get; set; }
}

public class ArriveGroupDto
{
    public int CodMode { get; set; }
    public required string Line { get; set; }
    public string? LineCode { get; set; }
    public int? Direction { get; set; }
    public int? Anden { get; set; }
    public required string Destination { get; set; }
    public required List<long> EstimatedArrives { get; set; }
}

public class ArriveDto
{
    public required string Line { get; set; }
    public string? LineCode { get; set; }
    public int? Direction { get; set; }
    public int CodMode { get; set; }
    public int? Anden { get; set; }
    public required string Destination { get; set; }
    public long EstimatedArrive { get; set; }

    public static List<ArriveGroupDto> GroupArrives(IEnumerable<ArriveDto> arrives)
    {
        return arrives
            .OrderBy(a => int.TryParse(a.Line, out var n) ? n : int.MaxValue)
            .GroupBy(a => (a.Line, a.Destination, a.Anden))
            .Select(g => new ArriveGroupDto
            {
                CodMode = g.First().CodMode,
                Line = g.First().Line,
                LineCode = g.First().LineCode,
                Direction = g.First().Direction,
                Anden = g.First().Anden,
                Destination = g.First().Destination,
                EstimatedArrives = g.Select(a => a.EstimatedArrive).ToList(),
            })
            .ToList();
    }
}

public class IncidentDto
{
    public required string Title { get; set; }
    public required string Description { get; set; }
    public required string From { get; set; }
    public required string To { get; set; }
    public required string Cause { get; set; }
    public required string Effect { get; set; }
    public required string Url { get; set; }
}

public class CoordinatesDto
{
    public double Latitude { get; set; }
    public double Longitude { get; set; }
}
