package echo;

/**
 * Turns raw user input into validated command arguments. Every method either
 * returns the parsed result or throws {@link EchoException} with a
 * user-facing explanation -- this class never prints anything itself.
 */
public class Parser {

    private Parser() {
    }

    /**
     * Returns the text after a command word (e.g. {@code "todo "}), rejecting
     * it if empty.
     *
     * @param userInput      the full command line.
     * @param commandWord    the command word without a trailing space, e.g. {@code "todo"}.
     * @param taskNounPhrase the task kind with its article, e.g. {@code "a todo"}, {@code "an event"}.
     * @return the trimmed, non-empty description.
     * @throws EchoException if no description follows the command word.
     */
    public static String requireDescription(String userInput, String commandWord, String taskNounPhrase)
            throws EchoException {
        String description = userInput.equals(commandWord)
                ? ""
                : userInput.substring(commandWord.length() + 1).trim();
        if (description.isEmpty()) {
            throw new EchoException("The description of " + taskNounPhrase + " cannot be empty.");
        }
        return description;
    }

    /**
     * Parses the text after {@code deadline }, of the form
     * {@code <description> /by <when>}.
     *
     * @param userInput the full command line starting with {@code deadline}.
     * @return the parsed deadline.
     * @throws EchoException if the description or the {@code /by} part is missing.
     */
    public static Deadline parseDeadline(String userInput) throws EchoException {
        String description = requireDescription(userInput, "deadline", "a deadline");
        String[] parts = description.split(" /by ", 2);
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new EchoException("A deadline needs a due date/time, e.g. "
                    + "\"deadline return book /by Sunday\".");
        }
        if (parts[0].trim().isEmpty()) {
            throw new EchoException("The description of a deadline cannot be empty.");
        }
        return new Deadline(parts[0].trim(), parts[1].trim());
    }

    /**
     * Parses the text after {@code event }, of the form
     * {@code <description> /from <start> /to <end>}.
     *
     * @param userInput the full command line starting with {@code event}.
     * @return the parsed event.
     * @throws EchoException if the description, {@code /from} or {@code /to} part is missing.
     */
    public static Event parseEvent(String userInput) throws EchoException {
        String description = requireDescription(userInput, "event", "an event");
        String[] fromParts = description.split(" /from ", 2);
        if (fromParts.length < 2 || fromParts[1].trim().isEmpty()) {
            throw new EchoException("An event needs a start and end time, e.g. "
                    + "\"event meeting /from Mon 2pm /to 4pm\".");
        }
        if (fromParts[0].trim().isEmpty()) {
            throw new EchoException("The description of an event cannot be empty.");
        }
        String[] toParts = fromParts[1].split(" /to ", 2);
        if (toParts.length < 2 || toParts[0].trim().isEmpty() || toParts[1].trim().isEmpty()) {
            throw new EchoException("An event needs both /from and /to, e.g. "
                    + "\"event meeting /from Mon 2pm /to 4pm\".");
        }
        return new Event(fromParts[0].trim(), toParts[0].trim(), toParts[1].trim());
    }

    /**
     * Parses and validates the task number after {@code mark}/{@code unmark}/{@code delete}.
     *
     * @param userInput the full command line, e.g. {@code "mark 2"}.
     * @param command   the command word, e.g. {@code "mark"}, {@code "unmark"} or {@code "delete"}.
     * @param taskCount how many tasks currently exist, for bounds checking.
     * @return the 1-based task number, guaranteed to be in {@code [1, taskCount]}.
     * @throws EchoException if the number is missing, not a number, or out of range.
     */
    public static int parseTaskNumber(String userInput, String command, int taskCount) throws EchoException {
        String argument = userInput.equals(command)
                ? ""
                : userInput.substring(command.length() + 1).trim();
        if (argument.isEmpty()) {
            throw new EchoException("Tell me which task number to " + command
                    + ", e.g. \"" + command + " 2\".");
        }
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            throw new EchoException("\"" + argument + "\" is not a task number.");
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new EchoException("There is no task number " + taskNumber + " in the list.");
        }
        return taskNumber;
    }
}
