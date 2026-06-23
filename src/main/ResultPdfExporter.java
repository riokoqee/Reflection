package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DeflaterOutputStream;

public final class ResultPdfExporter {

    private static final int PAGE_WIDTH = 1240;
    private static final int PAGE_HEIGHT = 1754;
    private static final int MARGIN = 92;
    private static final Color PAPER = new Color(247, 242, 231);
    private static final Color INK = new Color(34, 37, 38);
    private static final Color MUTED = new Color(86, 92, 90);
    private static final Color ACCENT = new Color(72, 121, 101);
    private static final Color GOLD = new Color(179, 127, 55);
    private static final String CONTACT_EMAIL = "rioko988@gmail.com";
    private static final String CONTACT_PHONE = "+77074418393";

    private ResultPdfExporter() {
    }

    public static File getResultsDirectory() throws IOException {
        File directory = new File(System.getProperty("user.dir"), "results");
        if (!directory.isDirectory() && !directory.mkdirs()) {
            throw new IOException("Cannot create results directory: " + directory.getAbsolutePath());
        }
        return directory.getAbsoluteFile();
    }

    public static File export(GamePanel gp) throws IOException {
        String timestamp = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm").format(LocalDateTime.now());
        String playerName = sanitizeFileName(gp.getPlayerNameForReport());
        File file = new File(getResultsDirectory(),
                "ReflectionResult" + playerName + "_" + timestamp + ".pdf");
        ArrayList<BufferedImage> pages = new Renderer(gp).render();
        writePdf(file, pages);
        return file.getAbsoluteFile();
    }

    private static String sanitizeFileName(String value) {
        String cleaned = value == null ? "" : value.trim().replaceAll("\\s+", "_");
        cleaned = cleaned.replaceAll("[^\\p{L}\\p{N}_-]", "");
        if (cleaned.isEmpty()) {
            cleaned = "Player";
        }
        if (cleaned.length() > 42) {
            cleaned = cleaned.substring(0, 42);
        }
        return cleaned;
    }

    private static void writePdf(File file, List<BufferedImage> pages) throws IOException {
        ArrayList<byte[]> pageImages = new ArrayList<>();
        for (BufferedImage page : pages) {
            pageImages.add(encodeRgbFlate(page));
        }

        try (FileOutputStream out = new FileOutputStream(file)) {
            ArrayList<Long> offsets = new ArrayList<>();
            writeAscii(out, "%PDF-1.4\n%\u00e2\u00e3\u00cf\u00d3\n");
            int objectCount = 2 + pages.size() * 3;

            offsets.add(0L);
            offsets.add(out.getChannel().position());
            writeObject(out, 1, "<< /Type /Catalog /Pages 2 0 R >>");

            offsets.add(out.getChannel().position());
            StringBuilder kids = new StringBuilder();
            for (int i = 0; i < pages.size(); i++) {
                kids.append(3 + i * 3).append(" 0 R ");
            }
            writeObject(out, 2, "<< /Type /Pages /Count " + pages.size() + " /Kids [" + kids + "] >>");

            for (int i = 0; i < pages.size(); i++) {
                int pageObject = 3 + i * 3;
                int imageObject = pageObject + 1;
                int contentObject = pageObject + 2;
                String imageName = "Im" + (i + 1);

                offsets.add(out.getChannel().position());
                writeObject(out, pageObject,
                        "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] " +
                                "/Resources << /XObject << /" + imageName + " " + imageObject + " 0 R >> >> " +
                                "/Contents " + contentObject + " 0 R >>");

                byte[] imageData = pageImages.get(i);
                offsets.add(out.getChannel().position());
                writeAscii(out, imageObject + " 0 obj\n");
                writeAscii(out, "<< /Type /XObject /Subtype /Image /Width " + PAGE_WIDTH +
                        " /Height " + PAGE_HEIGHT +
                        " /ColorSpace /DeviceRGB /BitsPerComponent 8 /Filter /FlateDecode /Length " +
                        imageData.length + " >>\nstream\n");
                out.write(imageData);
                writeAscii(out, "\nendstream\nendobj\n");

                byte[] content = ("q 595 0 0 842 0 0 cm /" + imageName + " Do Q\n")
                        .getBytes(StandardCharsets.US_ASCII);
                offsets.add(out.getChannel().position());
                writeAscii(out, contentObject + " 0 obj\n");
                writeAscii(out, "<< /Length " + content.length + " >>\nstream\n");
                out.write(content);
                writeAscii(out, "endstream\nendobj\n");
            }

            long xrefOffset = out.getChannel().position();
            writeAscii(out, "xref\n0 " + (objectCount + 1) + "\n");
            writeAscii(out, "0000000000 65535 f \n");
            for (int i = 1; i <= objectCount; i++) {
                writeAscii(out, String.format("%010d 00000 n \n", offsets.get(i)));
            }
            writeAscii(out, "trailer\n<< /Size " + (objectCount + 1) + " /Root 1 0 R >>\n");
            writeAscii(out, "startxref\n" + xrefOffset + "\n%%EOF\n");
        }
    }

    private static byte[] encodeRgbFlate(BufferedImage image) throws IOException {
        ByteArrayOutputStream compressed = new ByteArrayOutputStream(image.getWidth() * image.getHeight());
        try (DeflaterOutputStream out = new DeflaterOutputStream(compressed)) {
            byte[] row = new byte[image.getWidth() * 3];
            for (int y = 0; y < image.getHeight(); y++) {
                int offset = 0;
                for (int x = 0; x < image.getWidth(); x++) {
                    int rgb = image.getRGB(x, y);
                    row[offset++] = (byte) ((rgb >> 16) & 0xff);
                    row[offset++] = (byte) ((rgb >> 8) & 0xff);
                    row[offset++] = (byte) (rgb & 0xff);
                }
                out.write(row);
            }
        }
        return compressed.toByteArray();
    }

    private static void writeObject(FileOutputStream out, int number, String body) throws IOException {
        writeAscii(out, number + " 0 obj\n" + body + "\nendobj\n");
    }

    private static void writeAscii(FileOutputStream out, String text) throws IOException {
        out.write(text.getBytes(StandardCharsets.ISO_8859_1));
    }

    private static final class Renderer {

        private final GamePanel gp;
        private final ArrayList<BufferedImage> pages = new ArrayList<>();
        private BufferedImage page;
        private Graphics2D g;
        private int y;
        private int pageNumber = 0;

        Renderer(GamePanel gp) {
            this.gp = gp;
        }

        ArrayList<BufferedImage> render() {
            newPage();
            drawHeader();
            drawSummary();
            drawMetrics();
            drawWorldState();
            drawAnalysis();
            drawEntries();
            drawContacts();
            finishPage();
            return pages;
        }

        private void newPage() {
            if (page != null) {
                finishPage();
            }
            page = new BufferedImage(PAGE_WIDTH, PAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
            g = page.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setColor(PAPER);
            g.fillRect(0, 0, PAGE_WIDTH, PAGE_HEIGHT);
            pageNumber++;
            y = MARGIN;
        }

        private void finishPage() {
            if (g == null) {
                return;
            }
            g.setFont(GameFonts.regular(24));
            g.setColor(new Color(130, 122, 105));
            g.drawString("Reflection", MARGIN, PAGE_HEIGHT - 52);
            String number = label("стр. ", "page ") + pageNumber;
            g.drawString(number, PAGE_WIDTH - MARGIN - g.getFontMetrics().stringWidth(number), PAGE_HEIGHT - 52);
            g.dispose();
            pages.add(page);
            page = null;
            g = null;
        }

        private void ensure(int height) {
            if (y + height > PAGE_HEIGHT - 120) {
                newPage();
            }
        }

        private void drawHeader() {
            g.setColor(new Color(17, 25, 28));
            g.fillRoundRect(MARGIN - 24, y - 30, PAGE_WIDTH - MARGIN * 2 + 48, 172, 28, 28);
            g.setColor(new Color(174, 215, 196));
            g.setStroke(new BasicStroke(4));
            g.drawRoundRect(MARGIN - 18, y - 24, PAGE_WIDTH - MARGIN * 2 + 36, 160, 24, 24);

            g.setFont(GameFonts.bold(58));
            g.setColor(Color.white);
            g.drawString("Reflection", MARGIN + 18, y + 42);
            g.setFont(GameFonts.semibold(34));
            g.setColor(new Color(231, 240, 235));
            g.drawString(label("Итоговый отчёт прохождения", "Final playthrough report"), MARGIN + 20, y + 88);

            g.setFont(GameFonts.regular(25));
            g.setColor(new Color(204, 216, 210));
            String date = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").format(LocalDateTime.now());
            g.drawString(label("Игрок: ", "Player: ") + gp.getPlayerNameForReport() + "  •  " +
                    label("Слот ", "Slot ") + gp.saveLoad.getCurrentSlot() + "  •  " + date,
                    MARGIN + 20, y + 124);
            y += 205;
        }

        private void drawSummary() {
            section(label("Профиль результата", "Result profile"));
            String profileText = gp.tr(gp.story.getProfileText());
            g.setFont(GameFonts.regular(25));
            int profileLines = countWrappedLines(profileText, PAGE_WIDTH - MARGIN * 2 - 56);
            int cardHeight = Math.max(220, 152 + profileLines * 32);
            int cardTop = y;
            cardStart(cardHeight);
            g.setFont(GameFonts.semibold(27));
            g.setColor(MUTED);
            g.drawString(label("Пользователь: ", "User: ") + gp.getPlayerNameForReport(), MARGIN + 28, y + 38);
            g.setFont(GameFonts.bold(34));
            g.setColor(ACCENT);
            g.drawString(gp.tr(gp.story.getProfileTitle()), MARGIN + 28, y + 78);
            g.setFont(GameFonts.regular(25));
            g.setColor(INK);
            drawText(profileText, MARGIN + 28, y + 118, PAGE_WIDTH - MARGIN * 2 - 56, 32);
            y = cardTop + cardHeight;
            y += 18;
        }

        private void drawMetrics() {
            ensure(390);
            y += 18;
            section(label("Метрики", "Metrics"));
            int startY = y;
            drawMetric(metricLabel(0), gp.story.growth, MARGIN, startY);
            drawMetric(metricLabel(1), gp.story.calm, MARGIN + 520, startY);
            drawMetric(metricLabel(2), gp.story.empathy, MARGIN, startY + 84);
            drawMetric(metricLabel(3), gp.story.confidence, MARGIN + 520, startY + 84);
            drawMetric(metricLabel(4), gp.story.responsibility, MARGIN, startY + 168);
            drawMetric(metricLabel(5), gp.story.avoidance, MARGIN + 520, startY + 168);
            drawMetric(metricLabel(6), gp.story.selfWorth, MARGIN, startY + 252);
            y = startY + 330;
        }

        private void drawAnalysis() {
            section(label("Статистика и аналитика", "Statistics and analysis"));
            int choices = gp.story.getReportChoiceCount();
            int events = gp.story.getReportEventCount();
            int memories = gp.story.getUnlockedMemoryCount();
            int totalMemories = gp.story.getTotalMemoryCount();
            String strongest = strongestMetric();
            String weakest = weakestMetric();
            int[] metricValues = {
                    gp.story.growth,
                    gp.story.calm,
                    gp.story.empathy,
                    gp.story.confidence,
                    gp.story.responsibility,
                    gp.story.avoidance,
                    gp.story.selfWorth
            };
            String[] metricLabels = {
                    metricLabel(0),
                    metricLabel(1),
                    metricLabel(2),
                    metricLabel(3),
                    metricLabel(4),
                    metricLabel(5),
                    metricLabel(6)
            };
            int cardTop = y;
            int cardHeight = 410;
            cardStart(cardHeight);
            g.setFont(GameFonts.regular(25));
            g.setColor(INK);
            int lineY = y + 36;
            int textWidth = 610;
            lineY = drawBullet(label("Выборов в диалогах: ", "Dialogue choices: ") + choices, MARGIN + 28, lineY);
            lineY = drawBullet(label("Событий и взаимодействий: ", "Events and interactions: ") + events, MARGIN + 28, lineY);
            lineY = drawBullet(label("Открыто воспоминаний: ", "Unlocked memories: ") + memories +
                    label(" из ", " of ") + totalMemories, MARGIN + 28, lineY);
            lineY = drawBullet(label("Самая сильная сторона: ", "Strongest side: ") + strongest, MARGIN + 28, lineY);
            lineY = drawBullet(label("Зона внимания: ", "Needs attention: ") + weakest, MARGIN + 28, lineY);
            lineY = drawBullet(label("Пирог справа показывает доли итоговых метрик в процентах.",
                    "The pie chart shows the final metric shares."), MARGIN + 28, lineY);
            drawPieChart(MARGIN + 830, cardTop + 132, 78, metricValues, metricLabels);
            drawText(label("Рекомендация: ", "Recommendation: ") + gp.tr(gp.story.getRecommendation()),
                    MARGIN + 28, lineY + 10, textWidth, 31);
            y = cardTop + cardHeight;
            y += 20;
        }

        private void drawWorldState() {
            section(label("Состояние мира", "World state"));
            int cardTop = y;
            int cardHeight = 280;
            cardStart(cardHeight);
            g.setFont(GameFonts.regular(25));
            g.setColor(INK);
            int lineY = y + 36;
            lineY = drawBullet(label("Текущая локация: ", "Current location: ") + gp.tr(gp.story.getLocationTitle()), MARGIN + 28, lineY);
            lineY = drawBullet(label("Фонарь у игрока: ", "Lantern carried: ") + yesNo(gp.hasLantern), MARGIN + 28, lineY);
            lineY = drawBullet(label("Лампа в спальне: ", "Bedroom lamp: ") +
                    (gp.bedroomLampOn ? label("включена", "on") : label("выключена", "off")), MARGIN + 28, lineY);
            lineY = drawBullet(label("Телевизор в зале: ", "Living room TV: ") +
                    (gp.tvOn ? label("включён", "on") : label("выключен", "off")), MARGIN + 28, lineY);
            lineY = drawBullet(label("Телефон найден: ", "Phone found: ") + yesNo(gp.story.isPhoneDresserOpen()), MARGIN + 28, lineY);
            lineY = drawBullet(label("Дополнительные события завершены: ", "Optional events completed: ") +
                    completedOptionalEvents() + label(" из 9", " of 9"), MARGIN + 28, lineY);
            y = Math.max(lineY + 8, cardTop + cardHeight);
            y += 20;
        }

        private String yesNo(boolean value) {
            return value ? label("да", "yes") : label("нет", "no");
        }

        private int completedOptionalEvents() {
            int count = 0;
            if (gp.story.phoneEventDone) count++;
            if (gp.story.photoEventDone) count++;
            if (gp.story.mirrorEventDone) count++;
            if (gp.story.lostLanternEventDone) count++;
            if (gp.story.woundedBirdEventDone) count++;
            if (gp.story.oldLetterEventDone) count++;
            if (gp.story.helpRequestEventDone) count++;
            if (gp.story.forkEventDone) count++;
            if (gp.story.travelerEventDone) count++;
            return count;
        }

        private void drawEntries() {
            section(label("Хронология прохождения", "Playthrough timeline"));
            ArrayList<ReportEntry> entries = gp.story.getReportEntries();
            if (entries.isEmpty()) {
                drawPlain(label("Для этого сохранения подробная история ещё не записана. Новые прохождения будут сохранять выборы и события автоматически.",
                        "This save does not yet contain a detailed history. New playthroughs will record choices and events automatically."));
                return;
            }
            for (ReportEntry entry : entries) {
                int estimated = estimateEntryHeight(entry);
                ensure(estimated);
                drawEntry(entry);
                y += 16;
            }
        }

        private void drawContacts() {
            ensure(300);
            y += 22;
            section(label("Обратная связь", "Feedback"));
            int cardTop = y;
            int cardHeight = 190;
            cardStart(cardHeight);
            g.setFont(GameFonts.regular(26));
            g.setColor(INK);
            y = drawText(label("Если отчёт нужно расширить, добавить новые графики, интерпретации или экспорт в другой формат, можно связаться для доработки.",
                            "If the report needs more charts, interpretation, or another export format, contact me for improvements."),
                    MARGIN + 28, y + 38, PAGE_WIDTH - MARGIN * 2 - 56, 33);
            g.setFont(GameFonts.semibold(27));
            g.setColor(ACCENT);
            g.drawString("Email: " + CONTACT_EMAIL, MARGIN + 28, y + 26);
            g.drawString(label("Телефон: ", "Phone: ") + CONTACT_PHONE, MARGIN + 28, y + 62);
            y = Math.max(y + 86, cardTop + cardHeight);
        }

        private void section(String title) {
            ensure(90);
            g.setFont(GameFonts.bold(36));
            g.setColor(new Color(48, 70, 65));
            g.drawString(title, MARGIN, y);
            g.setColor(GOLD);
            g.fillRect(MARGIN, y + 14, 160, 5);
            y += 42;
        }

        private void cardStart(int minHeight) {
            ensure(minHeight);
            g.setColor(new Color(255, 252, 244));
            g.fillRoundRect(MARGIN, y, PAGE_WIDTH - MARGIN * 2, minHeight, 20, 20);
            g.setColor(new Color(205, 192, 166));
            g.drawRoundRect(MARGIN, y, PAGE_WIDTH - MARGIN * 2, minHeight, 20, 20);
        }

        private void cardEnd() {
            y += 20;
        }

        private void drawPieChart(int centerX, int centerY, int radius, int[] values, String[] labels) {
            Color[] colors = {
                    new Color(72, 121, 101),
                    new Color(179, 127, 55),
                    new Color(118, 92, 150),
                    new Color(68, 114, 165),
                    new Color(178, 92, 91),
                    new Color(93, 139, 77),
                    new Color(190, 154, 68)
            };
            int total = 0;
            for (int value : values) {
                total += Math.max(0, value);
            }

            if (total <= 0) {
                g.setColor(new Color(219, 211, 190));
                g.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
                g.setColor(new Color(255, 252, 244));
                g.fillOval(centerX - radius / 2, centerY - radius / 2, radius, radius);
            }
            else {
                int start = 90;
                int used = 0;
                for (int i = 0; i < values.length; i++) {
                    int value = Math.max(0, values[i]);
                    int extent = i == values.length - 1
                            ? 360 - used
                            : Math.round(value * 360f / total);
                    g.setColor(colors[i % colors.length]);
                    g.fillArc(centerX - radius, centerY - radius, radius * 2, radius * 2, start, -extent);
                    start -= extent;
                    used += extent;
                }
                g.setColor(new Color(255, 252, 244));
                g.fillOval(centerX - radius / 3, centerY - radius / 3, radius * 2 / 3, radius * 2 / 3);
            }

            g.setColor(new Color(206, 194, 168));
            g.setStroke(new BasicStroke(3));
            g.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

            int legendX = centerX - radius - 8;
            int legendY = centerY + radius + 36;
            g.setFont(GameFonts.regular(18));
            for (int i = 0; i < labels.length; i++) {
                int rowY = legendY + i * 24;
                g.setColor(colors[i % colors.length]);
                g.fillRoundRect(legendX, rowY - 14, 18, 12, 5, 5);
                g.setColor(INK);
                g.drawString(labels[i] + ": " + Math.max(0, values[i]) + "%", legendX + 28, rowY);
            }
        }

        private void drawMetric(String label, int value, int x, int y) {
            int width = 420;
            g.setFont(GameFonts.semibold(26));
            g.setColor(INK);
            g.drawString(label, x, y);
            String number = value + "/100";
            g.drawString(number, x + width - g.getFontMetrics().stringWidth(number), y);
            int barY = y + 18;
            g.setColor(new Color(219, 211, 190));
            g.fillRoundRect(x, barY, width, 18, 18, 18);
            g.setColor(metricColor(value));
            g.fillRoundRect(x, barY, Math.max(8, value * width / 100), 18, 18, 18);
        }

        private Color metricColor(int value) {
            if (value >= 70) {
                return new Color(80, 151, 111);
            }
            if (value >= 45) {
                return new Color(190, 148, 67);
            }
            return new Color(178, 78, 72);
        }

        private void drawEntry(ReportEntry entry) {
            int top = y;
            int height = estimateEntryHeight(entry);
            g.setColor(new Color(255, 252, 244));
            g.fillRoundRect(MARGIN, top, PAGE_WIDTH - MARGIN * 2, height, 18, 18);
            g.setColor("Выбор".equals(entry.type) ? ACCENT : GOLD);
            g.fillRoundRect(MARGIN, top, 9, height, 9, 9);
            g.setColor(new Color(206, 194, 168));
            g.drawRoundRect(MARGIN, top, PAGE_WIDTH - MARGIN * 2, height, 18, 18);

            int textX = MARGIN + 28;
            int textY = top + 34;
            g.setFont(GameFonts.bold(27));
            g.setColor(INK);
            g.drawString(entry.order + ". " + localizeReportText(entry.type) + " — " +
                    localizeReportText(entry.title), textX, textY);
            g.setFont(GameFonts.regular(22));
            g.setColor(MUTED);
            g.drawString(localizeReportText(entry.location), textX, textY + 28);

            g.setColor(INK);
            int bodyY = textY + 66;
            if (!entry.prompt.isEmpty()) {
                bodyY = drawText(label("Контекст: ", "Context: ") + localizeReportText(entry.prompt), textX, bodyY,
                        PAGE_WIDTH - MARGIN * 2 - 56, 27);
            }
            if (!entry.choice.isEmpty()) {
                bodyY = drawText(label("Выбор: ", "Choice: ") + localizeReportText(entry.choice), textX, bodyY + 8,
                        PAGE_WIDTH - MARGIN * 2 - 56, 27);
            }
            if (!entry.metricDelta.isEmpty()) {
                g.setFont(GameFonts.semibold(23));
                g.setColor(ACCENT);
                bodyY = drawText(label("Изменения: ", "Changes: ") + localizeReportText(entry.metricDelta), textX, bodyY + 8,
                        PAGE_WIDTH - MARGIN * 2 - 56, 28);
            }
            if (!entry.result.isEmpty()) {
                g.setFont(GameFonts.regular(22));
                g.setColor(INK);
                bodyY = drawText(label("Итог: ", "Result: ") + localizeReportText(entry.result), textX, bodyY + 8,
                        PAGE_WIDTH - MARGIN * 2 - 56, 27);
            }
            if (!entry.beforeMetrics.isEmpty() && !entry.afterMetrics.isEmpty()) {
                g.setFont(GameFonts.regular(19));
                g.setColor(MUTED);
                bodyY = drawText(label("До: ", "Before: ") + localizeReportText(entry.beforeMetrics), textX, bodyY + 8,
                        PAGE_WIDTH - MARGIN * 2 - 56, 23);
                drawText(label("После: ", "After: ") + localizeReportText(entry.afterMetrics), textX, bodyY + 4,
                        PAGE_WIDTH - MARGIN * 2 - 56, 23);
            }
            y = top + height;
        }

        private int estimateEntryHeight(ReportEntry entry) {
            int textWidth = PAGE_WIDTH - MARGIN * 2 - 56;
            int height = 106;
            if (!entry.prompt.isEmpty()) {
                g.setFont(GameFonts.regular(22));
                height += countWrappedLines(label("Контекст: ", "Context: ") + localizeReportText(entry.prompt), textWidth) * 27 + 8;
            }
            if (!entry.choice.isEmpty()) {
                g.setFont(GameFonts.regular(22));
                height += countWrappedLines(label("Выбор: ", "Choice: ") + localizeReportText(entry.choice), textWidth) * 27 + 8;
            }
            if (!entry.metricDelta.isEmpty()) {
                g.setFont(GameFonts.semibold(23));
                height += countWrappedLines(label("Изменения: ", "Changes: ") + localizeReportText(entry.metricDelta), textWidth) * 28 + 8;
            }
            if (!entry.result.isEmpty()) {
                g.setFont(GameFonts.regular(22));
                height += countWrappedLines(label("Итог: ", "Result: ") + localizeReportText(entry.result), textWidth) * 27 + 8;
            }
            if (!entry.beforeMetrics.isEmpty() && !entry.afterMetrics.isEmpty()) {
                g.setFont(GameFonts.regular(19));
                height += countWrappedLines(label("До: ", "Before: ") + localizeReportText(entry.beforeMetrics), textWidth) * 23 + 4;
                height += countWrappedLines(label("После: ", "After: ") + localizeReportText(entry.afterMetrics), textWidth) * 23 + 4;
            }
            return Math.max(146, height + 18);
        }

        private int countWrappedLines(String text, int width) {
            if (text == null || text.isEmpty()) {
                return 0;
            }
            return Math.max(1, wrap(text, g.getFontMetrics(), width).size());
        }

        private int drawBullet(String text, int x, int y) {
            g.setColor(ACCENT);
            g.fillOval(x, y - 15, 10, 10);
            g.setColor(INK);
            g.drawString(text, x + 22, y);
            return y + 34;
        }

        private void drawPlain(String text) {
            ensure(130);
            g.setFont(GameFonts.regular(25));
            g.setColor(INK);
            y = drawText(text, MARGIN, y, PAGE_WIDTH - MARGIN * 2, 32) + 20;
        }

        private int drawText(String text, int x, int startY, int width, int lineHeight) {
            FontMetrics metrics = g.getFontMetrics();
            int lineY = startY;
            for (String line : wrap(text, metrics, width)) {
                g.drawString(line, x, lineY);
                lineY += lineHeight;
            }
            return lineY;
        }

        private ArrayList<String> wrap(String text, FontMetrics metrics, int width) {
            ArrayList<String> lines = new ArrayList<>();
            if (text == null || text.isEmpty()) {
                return lines;
            }
            for (String paragraph : text.split("\n", -1)) {
                String current = "";
                for (String word : paragraph.trim().split("\\s+")) {
                    if (word.isEmpty()) {
                        continue;
                    }
                    String next = current.isEmpty() ? word : current + " " + word;
                    if (metrics.stringWidth(next) <= width) {
                        current = next;
                    }
                    else {
                        if (!current.isEmpty()) {
                            lines.add(current);
                        }
                        current = word;
                    }
                }
                if (!current.isEmpty()) {
                    lines.add(current);
                }
            }
            return lines;
        }

        private String strongestMetric() {
            String[] names = {metricLabel(0), metricLabel(1), metricLabel(2), metricLabel(3), metricLabel(4), metricLabel(6)};
            int[] values = {gp.story.growth, gp.story.calm, gp.story.empathy, gp.story.confidence,
                    gp.story.responsibility, gp.story.selfWorth};
            int best = 0;
            for (int i = 1; i < values.length; i++) {
                if (values[i] > values[best]) {
                    best = i;
                }
            }
            return names[best] + " (" + values[best] + ")";
        }

        private String weakestMetric() {
            String[] names = {metricLabel(0), metricLabel(1), metricLabel(2), metricLabel(3), metricLabel(4), metricLabel(6)};
            int[] values = {gp.story.growth, gp.story.calm, gp.story.empathy, gp.story.confidence,
                    gp.story.responsibility, gp.story.selfWorth};
            int weakest = 0;
            for (int i = 1; i < values.length; i++) {
                if (values[i] < values[weakest]) {
                    weakest = i;
                }
            }
            return names[weakest] + " (" + values[weakest] + ")";
        }

        private String label(String ru, String en) {
            return gp.isEnglish() ? en : ru;
        }

        private String metricLabel(int index) {
            switch (index) {
                case 0:
                    return label("Рост", "Growth");
                case 1:
                    return label("Покой", "Calm");
                case 2:
                    return label("Эмпатия", "Empathy");
                case 3:
                    return label("Уверенность", "Confidence");
                case 4:
                    return label("Ответственность", "Responsibility");
                case 5:
                    return label("Избегание", "Avoidance");
                case 6:
                    return label("Самоценность", "Self-worth");
                default:
                    return "";
            }
        }

        private String localizeReportText(String text) {
            if (text == null || text.isEmpty()) {
                return "";
            }
            if (!gp.isEnglish()) {
                return text;
            }

            String localized = gp.tr(text);
            localized = localized.replace("Диалог:", "Dialogue:");
            localized = localized.replace("Событие", "Event");
            localized = localized.replace("Выбор", "Choice");
            localized = localized.replace("Метрики не изменились", "Metrics did not change");
            localized = localized.replace("Ответственность", "Responsibility");
            localized = localized.replace("Уверенность", "Confidence");
            localized = localized.replace("Самоценность", "Self-worth");
            localized = localized.replace("Избегание", "Avoidance");
            localized = localized.replace("Эмпатия", "Empathy");
            localized = localized.replace("Покой", "Calm");
            localized = localized.replace("Рост", "Growth");
            localized = localized.replace("Старик", "Elder");
            localized = localized.replace("Тень", "Shadow");
            localized = localized.replace("Ребёнок", "Child");
            localized = localized.replace("Друг", "Friend");
            localized = localized.replace("Воин", "Warrior");
            return localized;
        }
    }
}
