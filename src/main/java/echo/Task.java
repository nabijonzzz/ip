package echo;

import java.time.LocalDate;

/**
 * Represents a single task in the task list.
 * A task has a description and a status showing whether it is done.
 */
public class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates a task that is initially not done.
     *
     * @param description what the task is about.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns whether this task's description contains the given keyword.
     *
     * @param keyword the text to look for; matching is case-sensitive.
     */
    public boolean containsKeyword(String keyword) {
        return description.contains(keyword);
    }

    /**
     * Returns whether this task falls on the given date. A plain task has no
     * date, so it never does; subclasses with dates override this.
     *
     * @param date the date to check.
     */
    public boolean isOn(LocalDate date) {
        return false;
    }

    /**
     * Returns this task as {@code [X] description} when done,
     * or {@code [ ] description} when not done.
     */
    @Override
    public String toString() {
        String statusIcon = isDone ? "X" : " ";
        return "[" + statusIcon + "] " + description;
    }

    /**
     * Returns this task's save-file fields -- whether it is done, then the
     * description -- separated by {@code " | "}. Subclasses prepend their
     * type letter and append their own fields.
     */
    protected String toSaveFormat() {
        return (isDone ? "1" : "0") + " | " + description;
    }
}
