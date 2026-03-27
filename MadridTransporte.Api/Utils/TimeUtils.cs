namespace MadridTransporte.Api.Utils;

public static class TimeUtils
{
    private static readonly TimeZoneInfo MadridTimeZone = TimeZoneInfo.FindSystemTimeZoneById(
        "Romance Standard Time"
    );

    public static DateTimeOffset GetMadridNow() =>
        TimeZoneInfo.ConvertTime(DateTimeOffset.UtcNow, MadridTimeZone);

    public static TimeZoneInfo GetMadridTimeZone() => MadridTimeZone;
}
