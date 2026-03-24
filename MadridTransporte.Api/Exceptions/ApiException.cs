namespace MadridTransporte.Api.Exceptions;

public class ApiException(string message, int statusCode) : Exception(message)
{
    public int StatusCode { get; } = statusCode;

    public static ApiException NotFound(string? message = null) =>
        new(message ?? "Not found", 404);

    public static ApiException BadRequest(string? message = null) =>
        new(message ?? "Bad request", 400);

    public static ApiException InternalServerError(string? message = null) =>
        new(message ?? "Internal server error", 500);

    public static ApiException ServiceUnavailable(string? message = null) =>
        new(message ?? "Service unavailable", 503);

    public static ApiException Conflict(string? message = null) =>
        new(message ?? "Conflict", 409);

    public static ApiException TooManyRequests(string? message = null) =>
        new(message ?? "Too many requests", 429);
}
