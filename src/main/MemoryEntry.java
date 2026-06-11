package main;

public final class MemoryEntry {

    public final String id;
    public final String title;
    public final String location;
    public final String text;
    public final boolean unlocked;

    MemoryEntry(String id, String title, String location, String text, boolean unlocked) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.text = text;
        this.unlocked = unlocked;
    }
}
