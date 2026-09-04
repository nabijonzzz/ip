import java.util.Scanner;

/**
 * Echo is a command-line chatbot that manages a simple task list.
 *
 * <p>After greeting the user it repeatedly reads one line of input and acts on it:
 * {@code todo}, {@code deadline ... /by ...} and {@code event ... /from ... /to ...}
 * add tasks, {@code list} prints them, {@code mark <n>} / {@code unmark <n>} change a
 * task's done status, and {@code bye} exits.
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
            } else if (userInput.equals("list")) {
                printAllTasks();
            } else if (userInput.startsWith("mark ")) {
                markTask(Integer.parseInt(userInput.substring("mark ".length()).trim()));
            } else if (userInput.startsWith("unmark ")) {
                unmarkTask(Integer.parseInt(userInput.substring("unmark ".length()).trim()));
            } else if (userInput.startsWith("todo ")) {
                addTask(new Todo(userInput.substring("todo ".length()).trim()));
            } else if (userInput.startsWith("deadline ")) {
                addTask(parseDeadline(userInput.substring("deadline ".length())));
            } else if (userInput.startsWith("event ")) {
                addTask(parseEvent(userInput.substring("event ".length())));
            } else {
                addTask(new Task(userInput));
            }
        }
        printMessage("Bye. Hope to see you again soon!");
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
     */
    private static void addTask(Task task) {
        tasks[taskCount] = task;
        taskCount++;
        printMessage("Got it. I've added this task:" + System.lineSeparator()
                + "  " + task + System.lineSeparator()
                + "Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Parses the text after {@code deadline }, of the form
     * {@code <description> /by <when>}.
     *
     * @param arguments command text with the command word removed.
     * @return the parsed deadline.
     */
    private static Deadline parseDeadline(String arguments) {
        String[] parts = arguments.split(" /by ", 2);
        return new Deadline(parts[0].trim(), parts[1].trim());
    }

    /**
     * Parses the text after {@code event }, of the form
     * {@code <description> /from <start> /to <end>}.
     *
     * @param arguments command text with the command word removed.
     * @return the parsed event.
     */
    private static Event parseEvent(String arguments) {
        String[] fromParts = arguments.split(" /from ", 2);
        String[] toParts = fromParts[1].split(" /to ", 2);
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
