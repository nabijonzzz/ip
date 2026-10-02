package echo;

import java.time.LocalDate;

/**
 * Represents an event: a task that spans from a start date to an end date.
 */
public class Event extends Task {
    private final LocalDate from;
    private final LocalDate to;

    /**
     * Creates an event that is initially not done.
     *
     * @param description what the event is.
     * @param from the date the event starts.
     * @param to the date the event ends; not before {@code from}.
     */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns this event as {@code [E]<base task string> (from: <date> to: <date>)},
     * with dates shown as e.g. {@code Oct 15 2019}.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + Dates.format(from) + " to: " + Dates.format(to) + ")";
    }

    @Override
    protected String toSaveFormat() {
        return "E | " + super.toSaveFormat() + " | " + from + " | " + to;
    }

    /**
     * Returns whether the given date is within this event, inclusive of its
     * start and end dates.
     *
     * @param date the date to check.
     */
    @Override
    public boolean isOn(LocalDate date) {
        return !date.isBefore(from) && !date.isAfter(to);
    }
}
