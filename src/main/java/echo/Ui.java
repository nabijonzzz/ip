package echo;

import java.util.List;
import java.util.Scanner;

/**
 * Handles all interaction with the user: printing messages and reading input.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final String BANNER = " _____ ____ _   _  ___  \n"
            + "| ____/ ___| | | |/ _ \\ \n"
            + "|  _|| |   | |_| | | | |\n"
            + "| |__| |___|  _  | |_| |\n"
            + "|_____\\____|_| |_|\\___/ \n";
    private static final String NAME = "Echo";

    private final Scanner scanner = new Scanner(System.in);

    /** Prints the greeting banner and the welcome message. */
    public void showGreeting() {
        showMessage(BANNER + System.lineSeparator()
                + "Hello! I'm " + NAME + "." + System.lineSeparator()
                + "What can I do for you?");
    }

    /** Prints the farewell message. */
    public void showGoodbye() {
        showMessage("Bye. Hope to see you again soon!");
    }

    /**
     * Reads one line of user input.
     *
     * @return the line read, or {@code null} if there is no more input.
     */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    /**
     * Prints an error message, prefixed with {@code OOPS!!! }.
     *
     * @param errorMessage what went wrong.
     */
    public void showError(String errorMessage) {
        showMessage("OOPS!!! " + errorMessage);
    }

    /**
     * Prints every task in the list, numbered, in response to the {@code list} command.
     *
     * @param tasks the tasks to display, in order.
     */
    public void showTaskList(List<Task> tasks) {
        StringBuilder taskList = new StringBuilder("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            taskList.append(System.lineSeparator())
                    .append(i + 1).append(".").append(tasks.get(i));
        }
        showMessage(taskList.toString());
    }

    /**
     * Confirms that a task was added.
     *
     * @param task       the task that was added.
     * @param totalTasks how many tasks are now in the list.
     */
    public void showTaskAdded(Task task, int totalTasks) {
        showMessage("Got it. I've added this task:" + System.lineSeparator()
                + "  " + task + System.lineSeparator()
                + "Now you have " + totalTasks + " tasks in the list.");
    }

    /**
     * Confirms that a task was removed.
     *
     * @param task       the task that was removed.
     * @param totalTasks how many tasks remain in the list.
     */
    public void showTaskDeleted(Task task, int totalTasks) {
        showMessage("Noted. I've removed this task:" + System.lineSeparator()
                + "  " + task + System.lineSeparator()
                + "Now you have " + totalTasks + " tasks in the list.");
    }

    /**
     * Confirms that a task's done status changed.
     *
     * @param task   the task after the change.
     * @param isDone whether it was just marked done ({@code true}) or not done ({@code false}).
     */
    public void showTaskStatusChanged(Task task, boolean isDone) {
        String heading = isDone
                ? "Nice! I've marked this task as done:"
                : "OK, I've marked this task as not done yet:";
        showMessage(heading + System.lineSeparator() + "  " + task);
    }

    /**
     * Prints a message framed between two divider lines.
     *
     * @param message text to display between the dividers.
     */
    public void showMessage(String message) {
        System.out.println(LINE);
        System.out.println(message);
        System.out.println(LINE);
    }
}
