/**
 * Represents a to-do: a task with only a description and no date attached.
 */
public class Todo extends Task {

    /**
     * Creates a to-do that is initially not done.
     *
     * @param description what the to-do is about.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns this to-do as {@code [T]} followed by the base task string.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
