package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

final class UiGraphics {

    private static final Map<String, BufferedImage> GRADIENT_CACHE = new HashMap<>();

    private UiGraphics() {
    }

    static float easeIn(float progress) {
        float clamped = clamp01(progress);
        return clamped * clamped * clamped;
    }

    static float easeOut(float progress) {
        float clamped = clamp01(progress);
        float inverse = 1f - clamped;
        return 1f - inverse * inverse * inverse;
    }

    static float easeInOut(float progress) {
        float clamped = clamp01(progress);
        if (clamped < 0.5f) {
            return 4f * clamped * clamped * clamped;
        }
        float value = -2f * clamped + 2f;
        return 1f - value * value * value / 2f;
    }

    static void fillVerticalGradient(Graphics2D g2, int x, int y, int width, int height,
                                     Color top, Color bottom) {
        if (width <= 0 || height <= 0) {
            return;
        }

        BufferedImage gradient = getVerticalGradient(height, top, bottom);
        g2.drawImage(gradient, x, y, width, height, null);
    }

    static void fillHorizontalGradient(Graphics2D g2, int x, int y, int width, int height,
                                       Color left, Color right) {
        if (width <= 0 || height <= 0) {
            return;
        }

        BufferedImage gradient = getHorizontalGradient(width, left, right);
        g2.drawImage(gradient, x, y, width, height, null);
    }

    static void drawSubWindow(Graphics2D g2, int x, int y, int width, int height, Color color) {
        g2.setColor(color);
        g2.fillRoundRect(x, y, width, height, 18, 18);

        g2.setColor(new Color(255, 255, 255, 185));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x + 4, y + 4, width - 8, height - 8, 14, 14);
    }

    static int measureLinesHeight(ArrayList<String> lines, int lineHeight) {
        int height = 0;
        for (String line : lines) {
            height += line.isEmpty() ? lineHeight / 2 : lineHeight;
        }
        return height;
    }

    static int drawWrappedText(Graphics2D g2, String text, int x, int y, int maxWidth, int lineHeight) {
        ArrayList<String> lines = wrapTextLines(g2, text, maxWidth, g2.getFont());
        return drawTextLines(g2, lines, x, y, maxWidth, lineHeight, Integer.MAX_VALUE);
    }

    static int drawTextLines(Graphics2D g2, ArrayList<String> lines, int x, int y,
                             int maxWidth, int lineHeight, int bottomY) {
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int nextY = y + (line.isEmpty() ? lineHeight / 2 : lineHeight);
            if (y > bottomY) {
                return y;
            }
            if (nextY > bottomY && i < lines.size() - 1) {
                g2.drawString(trimToWidth(g2, line, maxWidth), x, y);
                return nextY;
            }
            if (!line.isEmpty()) {
                g2.drawString(line, x, y);
            }
            y = nextY;
        }
        return y;
    }

    static ArrayList<String> wrapTextLines(Graphics2D g2, String text, int maxWidth, Font font) {
        FontMetrics fm = g2.getFontMetrics(font);
        ArrayList<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return lines;
        }

        String[] paragraphs = text.split("\n", -1);

        for (String paragraph : paragraphs) {
            if (paragraph.trim().isEmpty()) {
                lines.add("");
                continue;
            }

            String[] words = paragraph.trim().split("\\s+");
            String line = "";
            for (String word : words) {
                String testLine = line.isEmpty() ? word : line + " " + word;
                if (fm.stringWidth(testLine) <= maxWidth) {
                    line = testLine;
                }
                else if (!line.isEmpty()) {
                    lines.add(line);
                    line = fitWordToWidth(lines, word, maxWidth, fm);
                }
                else {
                    line = fitWordToWidth(lines, word, maxWidth, fm);
                }
            }

            if (!line.isEmpty()) {
                lines.add(line);
            }
        }
        return lines;
    }

    static void drawShadowedString(Graphics2D g2, String text, int x, int y,
                                   Color textColor, Color shadowColor) {
        g2.setColor(shadowColor);
        g2.drawString(text, x + 3, y + 3);
        g2.setColor(textColor);
        g2.drawString(text, x, y);
    }

    static String trimToWidth(Graphics2D g2, String text, int maxWidth) {
        FontMetrics fm = g2.getFontMetrics();
        if (fm.stringWidth(text) <= maxWidth) {
            return text;
        }

        String ellipsis = "...";
        while (!text.isEmpty() && fm.stringWidth(text + ellipsis) > maxWidth) {
            text = text.substring(0, text.length() - 1);
        }
        return text + ellipsis;
    }

    static int getCenteredX(Graphics2D g2, int screenWidth, String text) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return screenWidth / 2 - length / 2;
    }

    private static BufferedImage getVerticalGradient(int height, Color top, Color bottom) {
        String key = "v:" + height + ":" + top.getRGB() + ":" + bottom.getRGB();
        BufferedImage cached = GRADIENT_CACHE.get(key);
        if (cached != null) {
            return cached;
        }

        BufferedImage image = new BufferedImage(1, height, BufferedImage.TYPE_INT_ARGB);
        for (int row = 0; row < height; row++) {
            float progress = height <= 1 ? 1f : row / (float) (height - 1);
            image.setRGB(0, row, interpolateColor(top, bottom, progress).getRGB());
        }
        GRADIENT_CACHE.put(key, image);
        return image;
    }

    private static BufferedImage getHorizontalGradient(int width, Color left, Color right) {
        String key = "h:" + width + ":" + left.getRGB() + ":" + right.getRGB();
        BufferedImage cached = GRADIENT_CACHE.get(key);
        if (cached != null) {
            return cached;
        }

        BufferedImage image = new BufferedImage(width, 1, BufferedImage.TYPE_INT_ARGB);
        for (int col = 0; col < width; col++) {
            float progress = width <= 1 ? 1f : col / (float) (width - 1);
            image.setRGB(col, 0, interpolateColor(left, right, progress).getRGB());
        }
        GRADIENT_CACHE.put(key, image);
        return image;
    }

    private static Color interpolateColor(Color from, Color to, float progress) {
        float clampedProgress = clamp01(progress);
        int red = Math.round(from.getRed() + (to.getRed() - from.getRed()) * clampedProgress);
        int green = Math.round(from.getGreen() + (to.getGreen() - from.getGreen()) * clampedProgress);
        int blue = Math.round(from.getBlue() + (to.getBlue() - from.getBlue()) * clampedProgress);
        int alpha = Math.round(from.getAlpha() + (to.getAlpha() - from.getAlpha()) * clampedProgress);
        return new Color(red, green, blue, alpha);
    }

    private static String fitWordToWidth(ArrayList<String> lines, String word, int maxWidth, FontMetrics fm) {
        if (fm.stringWidth(word) <= maxWidth) {
            return word;
        }

        String line = "";
        for (int i = 0; i < word.length(); i++) {
            String next = line + word.charAt(i);
            if (fm.stringWidth(next) > maxWidth && !line.isEmpty()) {
                lines.add(line);
                line = String.valueOf(word.charAt(i));
            }
            else {
                line = next;
            }
        }
        return line;
    }

    private static float clamp01(float value) {
        return Math.max(0f, Math.min(1f, value));
    }
}
