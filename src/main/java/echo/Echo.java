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
            markTask(Parser.parseTaskNumber(userInput, "mark", tasks.size()));
        } else if (userInput.equals("unmark") || userInput.startsWith("unmark ")) {
            unmarkTask(Parser.parseTaskNumber(userInput, "unmark", tasks.size()));
        } else if (userInput.equals("delete") || userInput.startsWith("delete ")) {
            deleteTask(Parser.parseTaskNumber(userInput, "delete", tasks.size()));
        } else if (userInput.equals("todo") || userInput.startsWith("todo ")) {
            addTask(new Todo(Parser.requireDescription(userInput, "todo", "a todo")));
        } else if (userInput.equals("deadline") || userInput.startsWith("deadline ")) {
            addTask(Parser.parseDeadline(userInput));
        } else if (userInput.equals("event") || userInput.startsWith("event ")) {
            addTask(Parser.parseEvent(userInput));
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
