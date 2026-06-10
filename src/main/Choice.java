package main;

public final class Choice {

    public final String text;
    public final int growth;
    public final int calm;
    public final int empathy;
    public final int confidence;
    public final int responsibility;
    public final int avoidance;
    public final int selfWorth;
    public final String resultText;

    Choice(String text, int growth, int calm, int empathy, int confidence) {
        this(text, growth, calm, empathy, confidence, 0, 0, 0, "");
    }

    Choice(String text, int growth, int calm, int empathy, int confidence,
           int responsibility, int avoidance, int selfWorth, String resultText) {
        this.text = text;
        this.growth = growth;
        this.calm = calm;
        this.empathy = empathy;
        this.confidence = confidence;
        this.responsibility = responsibility;
        this.avoidance = avoidance;
        this.selfWorth = selfWorth;
        this.resultText = resultText;
    }
}
