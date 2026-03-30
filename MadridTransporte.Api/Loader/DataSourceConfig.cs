namespace MadridTransporte.Api.Loader;

public static class DataSourceConfig
{
    private static string Ref => Environment.GetEnvironmentVariable("TARGET") ?? "master";

    private static string GitHubBaseUrl =>
        $"https://github.com/xBaank/MadridTransporte-Backup/raw/refs/heads/{Ref}";

    // GTFS zip files
    public static string MetroGtfsUrl => $"{GitHubBaseUrl}/google_transit_M4.zip";
    public static string TrainGtfsUrl => $"{GitHubBaseUrl}/google_transit_M5.zip";
    public static string EmtGtfsUrl => $"{GitHubBaseUrl}/google_transit_M6.zip";
    public static string InterurbanGtfsUrl => $"{GitHubBaseUrl}/google_transit_M89.zip";
    public static string UrbanGtfsUrl => $"{GitHubBaseUrl}/google_transit_M9.zip";
    public static string TramGtfsUrl => $"{GitHubBaseUrl}/google_transit_M10.zip";

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

    public static string[] StopsInfoUrls => [MetroInfoUrl, TrainInfoUrl, TramInfoUrl];
}
