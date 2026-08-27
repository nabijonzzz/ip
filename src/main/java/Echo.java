import java.util.Scanner;

public class Echo {
    private static final String LINE = "____________________________________________________________";
    private static final String BANNER = " _____ ____ _   _  ___  \n"
            + "| ____/ ___| | | |/ _ \\ \n"
            + "|  _|| |   | |_| | | | |\n"
            + "| |__| |___|  _  | |_| |\n"
            + "|_____\\____|_| |_|\\___/ \n";
    private static final String NAME = "Echo";

    private static final String[] tasks = new String[100];
    private static int taskCount = 0;

    public static void main(String[] args) {
        printGreeting();
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String userInput = scanner.nextLine();
            if (userInput.equals("bye")) {
                break;
            }
            if (userInput.equals("list")) {
                printAllTasks();
            } else {
                addTask(userInput);
            }
        }
        printMessage("Bye. Hope to see you again soon!");
    }

    /**
     * Prints a Greeting message with ECHO banner
     */
    private static void printGreeting() {
        System.out.println(LINE);
        System.out.println(BANNER);
        System.out.println("Hello! I'm " + NAME + ".");
        System.out.println("What can I do for you?");
        System.out.println(LINE);
    }

    /**
     * Adds task to the task list and increments the task counter"
     *
     * @param task new task added by user
     */
    private static void addTask(String task) {
        tasks[taskCount] = task;
        taskCount++;
        printMessage("added: " + task);
    }

    /**
     * Prints the list of the tasks when user inputs the word list
     */
    private static void printAllTasks() {
        StringBuilder numberedList = new StringBuilder();
        for (int i = 0; i < taskCount; i++) {
            numberedList.append(i + 1).append(". ").append(tasks[i]);
            if (i < taskCount - 1) {
                numberedList.append(System.lineSeparator());
            }
        }
        printMessage(numberedList.toString());
    }

    /**
     * Prints message in between two lines
     *
     * @param message message to display
     */
    private static void printMessage(String message) {
        System.out.println(LINE);
        System.out.println(message);
        System.out.println(LINE);
    }

}
