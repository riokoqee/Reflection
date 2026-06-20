package main;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public final class GameFonts {

    private static final Font REGULAR = load("/font/cormorant/CormorantGaramond-Regular.ttf",
            new Font("Serif", Font.PLAIN, 16));
    private static final Font SEMIBOLD = load("/font/cormorant/CormorantGaramond-SemiBold.ttf",
            REGULAR.deriveFont(Font.BOLD, 16f));
    private static final Font BOLD = load("/font/cormorant/CormorantGaramond-Bold.ttf",
            REGULAR.deriveFont(Font.BOLD, 16f));
    private static final Map<String, Font> FONT_CACHE = new HashMap<>();

    private GameFonts() {
    }

    public static Font regular(float size) {
        return cached("regular", REGULAR, Font.PLAIN, size);
    }

    public static Font semibold(float size) {
        return cached("semibold", SEMIBOLD, Font.BOLD, size);
    }

    public static Font bold(float size) {
        return cached("bold", BOLD, Font.BOLD, size);
    }

    private static Font cached(String name, Font base, int style, float size) {
        String key = name + ":" + style + ":" + Float.floatToIntBits(size);
        Font font = FONT_CACHE.get(key);
        if (font == null) {
            font = base.deriveFont(style, size);
            FONT_CACHE.put(key, font);
        }
        return font;
    }

    private static Font load(String resourcePath, Font fallback) {
        try (InputStream stream = GameFonts.class.getResourceAsStream(resourcePath)) {
            if (stream == null) {
                return fallback;
            }
            Font font = Font.createFont(Font.TRUETYPE_FONT, stream);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
            return font;
        }
        catch (Exception e) {
            return fallback;
        }
    }
}
