package main;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;

public final class GameFonts {

    private static final Font REGULAR = load("/font/cormorant/CormorantGaramond-Regular.ttf",
            new Font("Serif", Font.PLAIN, 16));
    private static final Font SEMIBOLD = load("/font/cormorant/CormorantGaramond-SemiBold.ttf",
            REGULAR.deriveFont(Font.BOLD, 16f));
    private static final Font BOLD = load("/font/cormorant/CormorantGaramond-Bold.ttf",
            REGULAR.deriveFont(Font.BOLD, 16f));

    private GameFonts() {
    }

    public static Font regular(float size) {
        return REGULAR.deriveFont(Font.PLAIN, size);
    }

    public static Font semibold(float size) {
        return SEMIBOLD.deriveFont(Font.BOLD, size);
    }

    public static Font bold(float size) {
        return BOLD.deriveFont(Font.BOLD, size);
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
