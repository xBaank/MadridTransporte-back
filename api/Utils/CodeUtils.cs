namespace MadridTransporte.Api.Utils;

public static class CodeUtils
{
    public const string MetroCodMode = "4";
    public const string TrainCodMode = "5";
    public const string EmtCodMode = "6";
    public const string BusCodMode = "8";
    public const string UrbanCodMode = "9";
    public const string TramCodMode = "10";

    public static string CreateLineCode(string codMode, string lineCode) => $"{codMode}__{lineCode}___";
    public static string CreateStopCode(string codMode, string stopCode) => $"{codMode}_{stopCode}";
    public static string GetCodModeFromLineCode(string input) => input.Split("__")[0];
    public static string GetSimpleLineCodeFromLineCode(string input) => input.Split("__")[1].Split("___")[0];
    public static string GetStopCodeFromFullStopCode(string input) => input[(input.IndexOf('_') + 1)..];
    public static string GetCodModeFromFullStopCode(string input) => input.Split('_')[0];
}
