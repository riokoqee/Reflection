package main;

import java.io.Serializable;

public class ReportEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    public final int order;
    public final String type;
    public final String location;
    public final String title;
    public final String prompt;
    public final String choice;
    public final String result;
    public final String metricDelta;
    public final String beforeMetrics;
    public final String afterMetrics;

    public ReportEntry(int order, String type, String location, String title, String prompt,
                       String choice, String result, String metricDelta,
                       String beforeMetrics, String afterMetrics) {
        this.order = order;
        this.type = clean(type);
        this.location = clean(location);
        this.title = clean(title);
        this.prompt = clean(prompt);
        this.choice = clean(choice);
        this.result = clean(result);
        this.metricDelta = clean(metricDelta);
        this.beforeMetrics = clean(beforeMetrics);
        this.afterMetrics = clean(afterMetrics);
    }

    private static String clean(String value) {
        return value == null ? "" : value;
    }
}
