/**
 * Represents an event: a task that spans a start and an end date/time.
 * Both are kept as free text.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an event that is initially not done.
     *
     * @param description what the event is.
     * @param from when the event starts (free text).
     * @param to when the event ends (free text).
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns this event as {@code [E]<base task string> (from: <from> to: <to>)}.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
