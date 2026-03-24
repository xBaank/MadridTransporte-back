namespace MadridTransporte.Api.Clients.Train;

public class ElCanoAuthHandler : DelegatingHandler
{
    private const string AccessKey = "and20210615";
    private const string SecretKey = "Jthjtr946RTt";
    private const string UserId = "718da3df4199ede4";

    protected override async Task<HttpResponseMessage> SendAsync(HttpRequestMessage request, CancellationToken cancellationToken)
    {
        var uri = request.RequestUri!;
        var payload = request.Content != null ? await request.Content.ReadAsStringAsync(cancellationToken) : "";

        var auth = new ElCanoAuth(
            accessKey: AccessKey,
            secretKey: SecretKey,
            host: uri.Host,
            path: uri.AbsolutePath,
            httpMethod: request.Method.Method,
            contentType: "application/json;charset=utf-8",
            xElcanoClient: "AndroidElcanoApp",
            xElcanoUserId: UserId,
            payload: payload,
            queryParams: uri.Query.TrimStart('?'));

        var headers = auth.GetHeaders();
        foreach (var (key, value) in headers)
        {
            if (key.Equals("Content-type", StringComparison.OrdinalIgnoreCase))
            {
                if (request.Content != null)
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
