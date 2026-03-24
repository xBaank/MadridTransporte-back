using System.Linq;

namespace MadridTransporte.Api.Loader;

public static class DataSourceConfig
{
    private static string Ref => Environment.GetEnvironmentVariable("TARGET") ?? "master";

    private static string GitHubBaseUrl =>
        $"https://github.com/xBaank/MadridTransporte-Backup/raw/refs/heads/{Ref}";

    private static string ArcGisItemUrl(string id) =>
        $"https://www.arcgis.com/sharing/rest/content/items/{id}/data";

    // GTFS zip files
    public static string MetroGtfsUrl => ArcGisItemUrl("5c7f2951962540d69ffe8f640d94c246");
    public static string TrainGtfsUrl => ArcGisItemUrl("1a25440bf66f499bae2657ec7fb40144");
    public static string TramGtfsUrl => ArcGisItemUrl("aaed26cc0ff64b0c947ac0bc3e033196");
    public static string EmtGtfsUrl => ArcGisItemUrl("868df0e58fca47e79b942902dffd7da0");
    public static string UrbanGtfsUrl => ArcGisItemUrl("357e63c2904f43aeb5d8a267a64346d8");
    public static string InterurbanGtfsUrl => ArcGisItemUrl("885399f83408473c8d815e40c5e702b7");

    // CSV files
    public static string MetroInfoUrl => $"{GitHubBaseUrl}/Metro_stations.csv";
    public static string TrainInfoUrl => $"{GitHubBaseUrl}/Train_stations.csv";
    public static string TramInfoUrl => $"{GitHubBaseUrl}/Tram_stations.csv";
    public static string TrainItinerariesUrl => $"{GitHubBaseUrl}/Train_itineraries.csv";

    public record GtfsFeed(string Url, int CodMode);

    public static GtfsFeed[] AllGtfsFeeds =>
    [
        new(MetroGtfsUrl, 4),
        new(TrainGtfsUrl, 5),
        new(TramGtfsUrl, 10),
        new(InterurbanGtfsUrl, 8),
        new(UrbanGtfsUrl, 9),
        new(EmtGtfsUrl, 6),
    ];

    public static string[] AllGtfsUrls => AllGtfsFeeds.Select(f => f.Url).ToArray();

    public static string[] StopsInfoUrls => [MetroInfoUrl, TrainInfoUrl, TramInfoUrl];
}
