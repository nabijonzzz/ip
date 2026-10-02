package echo;

/**
 * Echo is a command-line chatbot that manages a simple task list.
 *
 * <p>After greeting the user it repeatedly reads one line of input and acts on it:
 * {@code todo}, {@code deadline ... /by yyyy-mm-dd} and
 * {@code event ... /from yyyy-mm-dd /to yyyy-mm-dd} add tasks, {@code list} prints them,
 * {@code find <keyword>} prints the ones whose description contains the keyword,
 * {@code on yyyy-mm-dd} prints the deadlines and events on that date,
 * {@code mark <n>} / {@code unmark <n>} change a
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
     * Greets the user, then repeatedly reads, parses and executes one command
     * at a time until a command signals exit ({@code bye} or end of input).
     */
    public void run() {
        ui.showGreeting();
        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (EchoException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showLine();
            }
        }
    }

    /**
     * Starts the chatbot, saving to and loading from {@code data/echo.txt}
     * relative to the folder it is run from.
     *
     * @param args not used.
     */
    public static void main(String[] args) {
        new Echo("data/echo.txt").run();
    }
}
