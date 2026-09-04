/**
 * Represents a deadline: a task that must be done by a certain date/time.
 * The date/time is kept as free text.
 */
public class Deadline extends Task {
    private final String by;

    /**
     * Creates a deadline that is initially not done.
     *
     * @param description what needs to be done.
     * @param by when it must be done by (free text).
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns this deadline as {@code [D]<base task string> (by: <by>)}.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
