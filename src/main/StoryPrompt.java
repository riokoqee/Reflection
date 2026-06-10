package main;

public final class StoryPrompt {

    public final String id;
    public final String speaker;
    public final String text;
    public final Choice[] choices;

    StoryPrompt(String id, String speaker, String text, Choice[] choices) {
        this.id = id;
        this.speaker = speaker;
        this.text = text;
        this.choices = choices;
    }
}
