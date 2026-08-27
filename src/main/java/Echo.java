import java.util.Scanner;

public class Echo {
    private static final String LINE = "____________________________________________________________";
    private static final String BANNER = " _____ ____ _   _  ___  \n"
            + "| ____/ ___| | | |/ _ \\ \n"
            + "|  _|| |   | |_| | | | |\n"
            + "| |__| |___|  _  | |_| |\n"
            + "|_____\\____|_| |_|\\___/ \n";
    private static final String NAME = "Echo";

    public static void main(String[] args) {
        printGreeting();
        Scanner scanner = new Scanner(System.in);

        while(scanner.hasNextLine()) {
            String userInput = scanner.nextLine();
            if(userInput.equals("bye")) {
                break;
            }
            printMessage(userInput);
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
