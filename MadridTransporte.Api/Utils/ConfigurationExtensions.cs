namespace MadridTransporte.Api.Utils;

public static class ConfigurationExtensions
{
    public static string GetRequired(this IConfiguration config, string key) =>
        config[key] is { Length: > 0 } value
            ? value
            : throw new InvalidOperationException($"Missing {key} configuration");
}
