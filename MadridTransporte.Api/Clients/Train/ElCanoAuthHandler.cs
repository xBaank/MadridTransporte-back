using MadridTransporte.Api.Utils;

namespace MadridTransporte.Api.Clients.Train;

public class ElCanoAuthHandler(IConfiguration config) : DelegatingHandler
{
    private string AccessKey => config.GetRequired("ElCano:AccessKey");
    private string SecretKey => config.GetRequired("ElCano:SecretKey");
    private string UserId => config.GetRequired("ElCano:UserId");
    private string Client => config["ElCano:Client"] is { Length: > 0 } c ? c : "AndroidElcanoApp";

    protected override async Task<HttpResponseMessage> SendAsync(
        HttpRequestMessage request,
        CancellationToken cancellationToken
    )
    {
        var uri = request.RequestUri!;
        var payload = request.Content is not null
            ? await request.Content.ReadAsStringAsync(cancellationToken)
            : "";

        var auth = new ElCanoAuth(
            accessKey: AccessKey,
            secretKey: SecretKey,
            host: uri.Host,
            path: uri.AbsolutePath,
            httpMethod: request.Method.Method,
            contentType: "application/json;charset=utf-8",
            xElcanoClient: Client,
            xElcanoUserId: UserId,
            payload: payload,
            queryParams: uri.Query.TrimStart('?')
        );

        var headers = auth.GetHeaders();
        foreach (var (key, value) in headers)
        {
            if (key.Equals("Content-type", StringComparison.OrdinalIgnoreCase))
            {
                if (request.Content is not null)
                {
                    request.Content.Headers.Remove("Content-Type");
                    request.Content.Headers.TryAddWithoutValidation("Content-Type", value);
                }
            }
            else
            {
                request.Headers.Remove(key);
                request.Headers.TryAddWithoutValidation(key, value);
            }
        }

        return await base.SendAsync(request, cancellationToken);
    }
}
