package main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResultReport implements Serializable {

    private static final long serialVersionUID = 1L;

    private final ArrayList<ReportEntry> entries = new ArrayList<>();
    private int nextOrder = 1;

    public void reset() {
        entries.clear();
        nextOrder = 1;
    }

    public void load(List<ReportEntry> loadedEntries) {
        reset();
        if (loadedEntries == null) {
            return;
        }
        entries.addAll(loadedEntries);
        for (ReportEntry entry : entries) {
            nextOrder = Math.max(nextOrder, entry.order + 1);
        }
    }

    public ArrayList<ReportEntry> copyEntries() {
        return new ArrayList<>(entries);
    }

    public void recordEvent(String location, String title, String result, String beforeMetrics, String afterMetrics) {
        entries.add(new ReportEntry(nextOrder++, "Событие", location, title, "", "", result,
                diffMetrics(beforeMetrics, afterMetrics), beforeMetrics, afterMetrics));
    }

    public void recordChoice(String location, StoryPrompt prompt, Choice choice,
                             String beforeMetrics, String afterMetrics) {
        String title = prompt.speaker == null || prompt.speaker.isEmpty()
                ? "Выбор"
                : "Диалог: " + prompt.speaker;
        entries.add(new ReportEntry(nextOrder++, "Выбор", location, title, prompt.text, choice.text,
                choice.resultText, diffMetrics(beforeMetrics, afterMetrics), beforeMetrics, afterMetrics));
    }

    public int countChoices() {
        int count = 0;
        for (ReportEntry entry : entries) {
            if ("Выбор".equals(entry.type)) {
                count++;
            }
        }
        return count;
    }

    public int countEvents() {
        return entries.size() - countChoices();
    }

    public static String metricsText(int growth, int calm, int empathy, int confidence,
                                     int responsibility, int avoidance, int selfWorth) {
        return "Рост " + growth +
                ", Покой " + calm +
                ", Эмпатия " + empathy +
                ", Уверенность " + confidence +
                ", Ответственность " + responsibility +
                ", Избегание " + avoidance +
                ", Самоценность " + selfWorth;
    }

    private String diffMetrics(String beforeMetrics, String afterMetrics) {
        int[] before = parseMetrics(beforeMetrics);
        int[] after = parseMetrics(afterMetrics);
        if (before.length == 0 || after.length == 0 || before.length != after.length) {
            return "";
        }

        String[] names = {"Рост", "Покой", "Эмпатия", "Уверенность", "Ответственность", "Избегание", "Самоценность"};
        ArrayList<String> parts = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            int delta = after[i] - before[i];
            if (delta != 0) {
                parts.add(names[i] + " " + (delta > 0 ? "+" : "") + delta);
            }
        }
        return parts.isEmpty() ? "Метрики не изменились" : String.join(", ", parts);
    }

    private int[] parseMetrics(String text) {
        if (text == null || text.isEmpty()) {
            return new int[0];
        }
        String[] parts = text.split(",");
        int[] values = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            String digits = parts[i].replaceAll("[^0-9-]", "");
            if (digits.isEmpty()) {
                return new int[0];
            }
            values[i] = Integer.parseInt(digits);
        }
        return values;
    }
}
