package echo;

import java.io.UncheckedIOException;

/**
 * Echo is a command-line chatbot that manages a simple task list.
 *
 * <p>After greeting the user it repeatedly reads one line of input and acts on it:
 * {@code todo}, {@code deadline ... /by ...} and {@code event ... /from ... /to ...}
 * add tasks, {@code list} prints them, {@code mark <n>} / {@code unmark <n>} change a
 * task's done status, {@code delete <n>} removes a task, and {@code bye} exits.
 * Invalid input is reported as an error instead of crashing the program. The
 * task list is saved to disk after every change and reloaded at startup.
 */
public class Echo {
    private final TaskList tasks;
    private final Storage storage;
    private final Ui ui;

    /**
     * Creates an Echo chatbot backed by the given save file, loading any
     * tasks already saved there.
     *
     * @param filePath path (relative to the project root) of the save file.
     */
    public Echo(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        tasks = new TaskList(storage.load());
    }

    /**
     * Greets the user, then repeatedly reads and acts on one command line at
     * a time until the user types {@code bye} or input ends.
     */
    public void run() {
        ui.showGreeting();
        while (true) {
            String userInput = ui.readCommand();
            if (userInput == null || userInput.equals("bye")) {
                break;
            }
            try {
                handleCommand(userInput);
            } catch (EchoException e) {
                ui.showError(e.getMessage());
            }
        }
        ui.showGoodbye();
    }

    public static void main(String[] args) {
        new Echo("data/echo.txt").run();
    }

    /**
     * Dispatches one command line to its handler.
     *
     * @param userInput the full line the user typed.
     * @throws EchoException if the line is not a recognized command, or the
     *                       command is recognized but its arguments are invalid.
     */
    private void handleCommand(String userInput) throws EchoException {
        if (userInput.equals("list")) {
            ui.showTaskList(tasks.asList());
        } else if (userInput.equals("mark") || userInput.startsWith("mark ")) {
            markTask(parseTaskNumber(userInput, "mark"));
        } else if (userInput.equals("unmark") || userInput.startsWith("unmark ")) {
            unmarkTask(parseTaskNumber(userInput, "unmark"));
        } else if (userInput.equals("delete") || userInput.startsWith("delete ")) {
            deleteTask(parseTaskNumber(userInput, "delete"));
        } else if (userInput.equals("todo") || userInput.startsWith("todo ")) {
            addTask(new Todo(requireDescription(userInput, "todo", "a todo")));
        } else if (userInput.equals("deadline") || userInput.startsWith("deadline ")) {
            addTask(parseDeadline(userInput));
        } else if (userInput.equals("event") || userInput.startsWith("event ")) {
            addTask(parseEvent(userInput));
        } else {
            throw new EchoException("I'm sorry, but I don't know what that means :-(");
        }
    }

    /**
     * Stores a task and confirms it to the user with the running total.
     *
     * @param task the task to add.
     */
    private void addTask(Task task) {
        tasks.add(task);
        ui.showTaskAdded(task, tasks.size());
        saveTasks();
    }

    /**
     * Removes the task at the given list position and confirms it to the user
     * with the running total.
     *
     * @param taskNumber 1-based position shown by the list command.
     */
    private void deleteTask(int taskNumber) {
        Task removed = tasks.remove(taskNumber - 1);
        ui.showTaskDeleted(removed, tasks.size());
        saveTasks();
    }

    /**
     * Persists the current task list to disk. A failure is reported to the
     * user rather than crashing the program -- the in-memory list is still
     * intact even if the save itself did not succeed.
     */
    private void saveTasks() {
        try {
            storage.save(tasks.asList());
        } catch (UncheckedIOException e) {
            ui.showError("Could not save your tasks: " + e.getCause().getMessage());
        }
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
    private String requireDescription(String userInput, String commandWord, String taskNounPhrase)
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
    private Deadline parseDeadline(String userInput) throws EchoException {
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
    private Event parseEvent(String userInput) throws EchoException {
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
     * @return the 1-based task number, guaranteed to point at an existing task.
     * @throws EchoException if the number is missing, not a number, or out of range.
     */
    private int parseTaskNumber(String userInput, String command) throws EchoException {
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
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new EchoException("There is no task number " + taskNumber + " in the list.");
        }
        return taskNumber;
    }

    /**
     * Marks the task at the given list position as done.
     *
     * @param taskNumber 1-based position shown by the list command.
     */
    private void markTask(int taskNumber) {
        Task task = tasks.get(taskNumber - 1);
        task.markAsDone();
        ui.showTaskStatusChanged(task, true);
        saveTasks();
    }

    /**
     * Marks the task at the given list position as not done.
     *
     * @param taskNumber 1-based position shown by the list command.
     */
    private void unmarkTask(int taskNumber) {
        Task task = tasks.get(taskNumber - 1);
        task.markAsNotDone();
        ui.showTaskStatusChanged(task, false);
        saveTasks();
    }
}
