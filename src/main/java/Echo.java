import java.util.Scanner;

/**
 * Echo is a command-line chatbot that manages a simple task list.
 *
 * <p>After greeting the user it repeatedly reads one line of input and acts on it:
 * {@code todo}, {@code deadline ... /by ...} and {@code event ... /from ... /to ...}
 * add tasks, {@code list} prints them, {@code mark <n>} / {@code unmark <n>} change a
 * task's done status, and {@code bye} exits. Invalid input is reported as an error
 * instead of crashing the program.
 */
public class Echo {
    private static final String LINE = "____________________________________________________________";
    private static final String BANNER = " _____ ____ _   _  ___  \n"
            + "| ____/ ___| | | |/ _ \\ \n"
            + "|  _|| |   | |_| | | | |\n"
            + "| |__| |___|  _  | |_| |\n"
            + "|_____\\____|_| |_|\\___/ \n";
    private static final String NAME = "Echo";

    /** Maximum number of tasks the list can hold. */
    private static final int MAX_TASKS = 100;
    private static final Task[] tasks = new Task[MAX_TASKS];
    private static int taskCount = 0;

    public static void main(String[] args) {
        printGreeting();
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String userInput = scanner.nextLine();
            if (userInput.equals("bye")) {
                break;
            }
            try {
                handleCommand(userInput);
            } catch (EchoException e) {
                printMessage("OOPS!!! " + e.getMessage());
            }
        }
        printMessage("Bye. Hope to see you again soon!");
    }

    /**
     * Dispatches one command line to its handler. The caller has already
     * checked for {@code bye}, so that command is not handled here.
     *
     * @param userInput the full line the user typed.
     * @throws EchoException if the line is not a recognized command, or the
     *                       command is recognized but its arguments are invalid.
     */
    private static void handleCommand(String userInput) throws EchoException {
        if (userInput.equals("list")) {
            printAllTasks();
        } else if (userInput.equals("mark") || userInput.startsWith("mark ")) {
            markTask(parseTaskNumber(userInput, "mark"));
        } else if (userInput.equals("unmark") || userInput.startsWith("unmark ")) {
            unmarkTask(parseTaskNumber(userInput, "unmark"));
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
     * Prints the greeting banner and the welcome message.
     */
    private static void printGreeting() {
        System.out.println(LINE);
        System.out.println(BANNER);
        System.out.println("Hello! I'm " + NAME + ".");
        System.out.println("What can I do for you?");
        System.out.println(LINE);
    }

    /**
     * Stores a task and confirms it to the user with the running total.
     *
     * @param task the task to add.
     * @throws EchoException if the list is already full.
     */
    private static void addTask(Task task) throws EchoException {
        if (taskCount >= MAX_TASKS) {
            throw new EchoException("The task list is full (max " + MAX_TASKS + " tasks).");
        }
        tasks[taskCount] = task;
        taskCount++;
        printMessage("Got it. I've added this task:" + System.lineSeparator()
                + "  " + task + System.lineSeparator()
                + "Now you have " + taskCount + " tasks in the list.");
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
    private static String requireDescription(String userInput, String commandWord, String taskNounPhrase)
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
    private static Deadline parseDeadline(String userInput) throws EchoException {
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
    private static Event parseEvent(String userInput) throws EchoException {
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
     * Prints every stored task as a numbered list, in response to the
     * {@code list} command.
     */
    private static void printAllTasks() {
        StringBuilder taskList = new StringBuilder("Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            taskList.append(System.lineSeparator())
                    .append(i + 1).append(".").append(tasks[i]);
        }
        printMessage(taskList.toString());
    }

    /**
     * Parses and validates the task number after {@code mark}/{@code unmark}.
     *
     * @param userInput the full command line, e.g. {@code "mark 2"}.
     * @param command   the command word, {@code "mark"} or {@code "unmark"}.
     * @return the 1-based task number, guaranteed to point at an existing task.
     * @throws EchoException if the number is missing, not a number, or out of range.
     */
    private static int parseTaskNumber(String userInput, String command) throws EchoException {
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

    /**
     * Marks the task at the given list position as done.
     *
     * @param taskNumber 1-based position shown by the list command.
     */
    private static void markTask(int taskNumber) {
        Task task = tasks[taskNumber - 1];
        task.markAsDone();
        printMessage("Nice! I've marked this task as done:" + System.lineSeparator()
                + "  " + task);
    }

    /**
     * Marks the task at the given list position as not done.
     *
     * @param taskNumber 1-based position shown by the list command.
     */
    private static void unmarkTask(int taskNumber) {
        Task task = tasks[taskNumber - 1];
        task.markAsNotDone();
        printMessage("OK, I've marked this task as not done yet:" + System.lineSeparator()
                + "  " + task);
    }

    /**
     * Prints a message framed between two divider lines.
     *
     * @param message text to display between the dividers.
     */
    private static void printMessage(String message) {
        System.out.println(LINE);
        System.out.println(message);
        System.out.println(LINE);
    }
}