using System.IO.Compression;

namespace MadridTransporte.Api.Loader;

public static class FileUtils
{
    public static async Task<string> DownloadToTempFileAsync(
        HttpClient httpClient,
        string url,
        ILogger logger
    )
    {
        logger.LogInformation("Downloading {Url}", url);
        var tempFile = Path.GetTempFileName();

        using var response = await httpClient.GetAsync(
            url,
            HttpCompletionOption.ResponseHeadersRead
        );
        if (!response.IsSuccessStatusCode)
        {
            var body = await response.Content.ReadAsStringAsync();
            throw new HttpRequestException(
                $"GET {url} failed with {(int)response.StatusCode} {response.ReasonPhrase}. Body: {body}"
            );
        }

        await using var stream = await response.Content.ReadAsStreamAsync();
        await using var fileStream = File.Create(tempFile);
        await stream.CopyToAsync(fileStream);

        logger.LogInformation("Downloaded {Url}", url);
        return tempFile;
    }

    public static string UnzipToTempDirectory(string zipFilePath)
    {
        var tempDir = Path.Combine(Path.GetTempPath(), Path.GetRandomFileName());
        Directory.CreateDirectory(tempDir);
        ZipFile.ExtractToDirectory(zipFilePath, tempDir);
        return tempDir;
    }

    /// <summary>
    /// Combines the same-named file from multiple GTFS directories into a single stream,
    /// skipping the header line on all but the first file.
    /// </summary>
    public static Stream CombineGtfsFiles(string fileName, IReadOnlyList<string> gtfsDirs)
    {
        var combined = new MemoryStream();
        using var writer = new StreamWriter(combined, leaveOpen: true);

        for (var i = 0; i < gtfsDirs.Count; i++)
        {
            var filePath = Path.Combine(gtfsDirs[i], fileName);
            if (!File.Exists(filePath))
                continue;

            using var reader = new StreamReader(filePath);
            var lineIndex = 0;
            while (reader.ReadLine() is { } line)
            {
                // Skip header on subsequent files
                if (i > 0 && lineIndex == 0)
                {
                    lineIndex++;
                    continue;
                }

                writer.WriteLine(line);
                lineIndex++;
            }
        }

        writer.Flush();
        combined.Position = 0;
        return combined;
    }

    /// <summary>
    /// Combines multiple CSV files into a single stream, skipping the header on all but the first.
    /// </summary>
    public static Stream CombineCsvFiles(IReadOnlyList<string> filePaths)
    {
        var combined = new MemoryStream();
        using var writer = new StreamWriter(combined, leaveOpen: true);

        for (var i = 0; i < filePaths.Count; i++)
        {
            if (!File.Exists(filePaths[i]))
                continue;

            using var reader = new StreamReader(filePaths[i]);
            var lineIndex = 0;
            while (reader.ReadLine() is { } line)
            {
                if (i > 0 && lineIndex == 0)
                {
                    lineIndex++;
                    continue;
                }

                writer.WriteLine(line);
                lineIndex++;
            }
        }

        writer.Flush();
        combined.Position = 0;
        return combined;
    }

    public static void CleanupTempFiles(IEnumerable<string> files, IEnumerable<string> directories)
    {
        foreach (var file in files)
        {
            try
            {
                if (File.Exists(file))
                    File.Delete(file);
            }
            catch
            {
                // Best effort cleanup
            }
        }

        foreach (var dir in directories)
        {
            try
            {
                if (Directory.Exists(dir))
                    Directory.Delete(dir, true);
            }
            catch
            {
                // Best effort cleanup
            }
        }
    }
}
