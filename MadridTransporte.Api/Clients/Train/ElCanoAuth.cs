using System.Globalization;
using System.Security.Cryptography;
using System.Text;

namespace MadridTransporte.Api.Clients.Train;

public class ElCanoAuth
{
    private const string ElcanoRequest = "elcano_request";
    private const string HeaderContentType = "Content-type";
    private const string HeaderXElcanoHost = "X-Elcano-Host";
    private const string HeaderXElcanoClient = "X-Elcano-Client";
    private const string HeaderXElcanoDate = "X-Elcano-Date";
    private const string HeaderXElcanoUserId = "X-Elcano-UserId";

    private readonly string _accessKey;
    private readonly string _secretKey;
    private readonly string _contentType;
    private readonly string _host;
    private readonly string _httpMethod;
    private readonly string _params;
    private readonly string _path;
    private readonly string _payload;
    private readonly string _xElcanoClient;
    private readonly string _xElcanoDate;
    private readonly string _xElcanoDateSimple;
    private readonly string _xElcanoUserId;

    public ElCanoAuth(
        string accessKey,
        string secretKey,
        string host,
        string path,
        string httpMethod,
        string contentType,
        string xElcanoClient,
        string xElcanoUserId,
        string payload,
        string queryParams
    )
    {
        _accessKey = accessKey;
        _secretKey = secretKey;
        _host = host;
        _path = path;
        _httpMethod = httpMethod;
        _contentType = contentType;
        _xElcanoClient = xElcanoClient;
        _xElcanoUserId = xElcanoUserId;
        _payload = payload.Replace(" ", "");
        _params = queryParams;

        var now = DateTime.UtcNow;
        _xElcanoDate = now.ToString("yyyyMMdd'T'HHmmss'Z'", CultureInfo.InvariantCulture);
        _xElcanoDateSimple = now.ToString("yyyyMMdd", CultureInfo.InvariantCulture);
    }

    public Dictionary<string, string> GetHeaders()
    {
        var headers = new Dictionary<string, string>
        {
            [HeaderXElcanoHost] = _host,
            [HeaderContentType] = _contentType,
            [HeaderXElcanoClient] = _xElcanoClient,
            [HeaderXElcanoDate] = _xElcanoDate,
            [HeaderXElcanoUserId] = _xElcanoUserId,
            ["Authorization"] = CalculateAuthorization(),
        };
        return headers;
    }

    private string CalculateAuthorization()
    {
        var canonicalRequest = PrepareCanonicalRequest();
        var stringToSign = PrepareStringToSign(canonicalRequest);
        var signature = CalculateSignature(stringToSign);
        return BuildAuthorizationString(signature);
    }

    private string PrepareCanonicalRequest()
    {
        var signedHeaders =
            $"{HeaderContentType.ToLower()};{HeaderXElcanoHost.ToLower()};{HeaderXElcanoClient.ToLower()};{HeaderXElcanoDate.ToLower()};{HeaderXElcanoUserId.ToLower()}";

        var sb = new StringBuilder();
        sb.Append(_httpMethod).Append('\n');
        sb.Append(_path).Append('\n');
        sb.Append(_params).Append('\n');
        sb.Append(HeaderContentType.ToLower()).Append(':').Append(_contentType).Append('\n');
        sb.Append(HeaderXElcanoHost.ToLower()).Append(':').Append(_host).Append('\n');
        sb.Append(HeaderXElcanoClient.ToLower()).Append(':').Append(_xElcanoClient).Append('\n');
        sb.Append(HeaderXElcanoDate.ToLower()).Append(':').Append(_xElcanoDate).Append('\n');
        sb.Append(HeaderXElcanoUserId.ToLower()).Append(':').Append(_xElcanoUserId).Append('\n');
        sb.Append(signedHeaders).Append('\n');

        var formattedPayload = _payload.Replace("\r", "").Replace("\n", "").Replace(" ", "");
        sb.Append(ToHex(formattedPayload));

        return sb.ToString();
    }

    private string PrepareStringToSign(string canonicalRequest) =>
        $"HMAC-SHA256\n{_xElcanoDate}\n{_xElcanoDateSimple}/{_xElcanoClient}/{_xElcanoUserId}/{ElcanoRequest}\n{ToHex(canonicalRequest)}";

    private string CalculateSignature(string stringToSign)
    {
        var signingKey = GetSignatureKey(_secretKey, _xElcanoDateSimple, _xElcanoClient);
        return BytesToHex(HmacSha256(signingKey, stringToSign));
    }

    private string BuildAuthorizationString(string signature)
    {
        var signedHeaders =
            $"{HeaderContentType.ToLower()};{HeaderXElcanoHost.ToLower()};{HeaderXElcanoClient.ToLower()};{HeaderXElcanoDate.ToLower()};{HeaderXElcanoUserId.ToLower()}";
        return $"HMAC-SHA256 Credential={_accessKey}/{_xElcanoDateSimple}/{_xElcanoClient}/{_xElcanoUserId}/{ElcanoRequest},SignedHeaders={signedHeaders},Signature={signature}";
    }

    private static string ToHex(string input)
    {
        var hash = SHA256.HashData(Encoding.UTF8.GetBytes(input));
        return Convert.ToHexStringLower(hash);
    }

    private static byte[] HmacSha256(byte[] key, string data)
    {
        using var hmac = new HMACSHA256(key);
        return hmac.ComputeHash(Encoding.UTF8.GetBytes(data));
    }

    private static byte[] GetSignatureKey(string secret, string date, string client)
    {
        var kDate = HmacSha256(Encoding.UTF8.GetBytes(secret), date);
        var kClient = HmacSha256(kDate, client);
        return HmacSha256(kClient, ElcanoRequest);
    }

    private static string BytesToHex(byte[] bytes) => Convert.ToHexStringLower(bytes);
}
