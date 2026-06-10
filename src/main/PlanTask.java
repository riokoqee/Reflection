package main;

public final class PlanTask {

    public final String text;
    public final String completedText;
    public final boolean completed;

    PlanTask(String text, String completedText, boolean completed) {
        this.text = text;
        this.completedText = completedText;
        this.completed = completed;
    }

    public String getDisplayText() {
        if (completed && completedText != null && !completedText.isEmpty()) {
            return completedText;
        }
        return text;
    }
}
