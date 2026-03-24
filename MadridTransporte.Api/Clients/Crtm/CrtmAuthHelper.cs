using System.Security.Cryptography;
using System.Text;

namespace MadridTransporte.Api.Clients.Crtm;

public static class CrtmAuthHelper
{
    private static readonly byte[] PrivateKey = Encoding.UTF8.GetBytes("pruebapruebapruebapruebaprueba12");
    private static readonly byte[] Iv = new byte[16];

    public static string Encrypt(byte[] publicKey)
    {
        using var aes = Aes.Create();
        aes.Key = PrivateKey;
        aes.IV = Iv;
        aes.Mode = CipherMode.CBC;
        aes.Padding = PaddingMode.PKCS7;

        using var encryptor = aes.CreateEncryptor();
        var encrypted = encryptor.TransformFinalBlock(publicKey, 0, publicKey.Length);
        return Convert.ToBase64String(encrypted);
    }
}
