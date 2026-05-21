param(
    [Parameter(Mandatory = $true)]
    [string]$InputPath,

    [Parameter(Mandatory = $true)]
    [string]$OutputDir,

    [string]$Prefix = "asset",
    [int]$MergeGap = 1,
    [int]$Padding = 0,
    [int]$MinPixels = 8,
    [switch]$Clean
)

$ErrorActionPreference = "Stop"

Add-Type -AssemblyName System.Drawing

Add-Type -ReferencedAssemblies "System.Drawing" -TypeDefinition @"
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Drawing.Imaging;
using System.IO;

public static class SpriteSheetSplitter
{
    public static int Split(string inputPath, string outputDir, string prefix, int mergeGap, int padding, int minPixels)
    {
        using (Bitmap source = new Bitmap(inputPath))
        {
            int width = source.Width;
            int height = source.Height;
            bool[] original = new bool[width * height];
            bool[] expanded = new bool[width * height];

            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    Color pixel = source.GetPixel(x, y);
                    if (pixel.A > 0)
                    {
                        original[y * width + x] = true;
                        for (int dy = -mergeGap; dy <= mergeGap; dy++)
                        {
                            int yy = y + dy;
                            if (yy < 0 || yy >= height) continue;

                            for (int dx = -mergeGap; dx <= mergeGap; dx++)
                            {
                                int xx = x + dx;
                                if (xx < 0 || xx >= width) continue;
                                expanded[yy * width + xx] = true;
                            }
                        }
                    }
                }
            }

            bool[] visited = new bool[width * height];
            int[] queue = new int[width * height];
            int saved = 0;

            for (int start = 0; start < expanded.Length; start++)
            {
                if (!expanded[start] || visited[start])
                {
                    continue;
                }

                int head = 0;
                int tail = 0;
                queue[tail++] = start;
                visited[start] = true;

                int minX = width;
                int minY = height;
                int maxX = -1;
                int maxY = -1;
                int opaqueCount = 0;

                while (head < tail)
                {
                    int index = queue[head++];
                    int x = index % width;
                    int y = index / width;

                    if (original[index])
                    {
                        opaqueCount++;
                        if (x < minX) minX = x;
                        if (x > maxX) maxX = x;
                        if (y < minY) minY = y;
                        if (y > maxY) maxY = y;
                    }

                    for (int oy = -1; oy <= 1; oy++)
                    {
                        int yy = y + oy;
                        if (yy < 0 || yy >= height) continue;

                        for (int ox = -1; ox <= 1; ox++)
                        {
                            if (ox == 0 && oy == 0) continue;

                            int xx = x + ox;
                            if (xx < 0 || xx >= width) continue;

                            int next = yy * width + xx;
                            if (expanded[next] && !visited[next])
                            {
                                visited[next] = true;
                                queue[tail++] = next;
                            }
                        }
                    }
                }

                if (opaqueCount < minPixels || maxX < minX || maxY < minY)
                {
                    continue;
                }

                minX = Math.Max(0, minX - padding);
                minY = Math.Max(0, minY - padding);
                maxX = Math.Min(width - 1, maxX + padding);
                maxY = Math.Min(height - 1, maxY + padding);

                int cropWidth = maxX - minX + 1;
                int cropHeight = maxY - minY + 1;

                using (Bitmap crop = new Bitmap(cropWidth, cropHeight, PixelFormat.Format32bppArgb))
                using (Graphics graphics = Graphics.FromImage(crop))
                {
                    graphics.Clear(Color.Transparent);
                    graphics.DrawImage(source, new Rectangle(0, 0, cropWidth, cropHeight), new Rectangle(minX, minY, cropWidth, cropHeight), GraphicsUnit.Pixel);

                    string fileName = String.Format("{0}_{1:D3}_x{2}_y{3}_{4}x{5}.png", prefix, saved, minX, minY, cropWidth, cropHeight);
                    crop.Save(Path.Combine(outputDir, fileName), ImageFormat.Png);
                }

                saved++;
            }

            return saved;
        }
    }
}
"@

$resolvedInput = (Resolve-Path -LiteralPath $InputPath).Path
New-Item -ItemType Directory -Force -Path $OutputDir | Out-Null

if ($Clean) {
    Get-ChildItem -LiteralPath $OutputDir -Filter "$Prefix`_*.png" -File -ErrorAction SilentlyContinue |
        Remove-Item -Force
}

$count = [SpriteSheetSplitter]::Split($resolvedInput, (Resolve-Path -LiteralPath $OutputDir).Path, $Prefix, $MergeGap, $Padding, $MinPixels)
Write-Output "Saved $count files to $OutputDir"
