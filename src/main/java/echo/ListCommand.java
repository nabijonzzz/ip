package echo;

/**
 * Shows every task in the list.
 */
public class ListCommand extends Command {

    /** Shows every task in the list, numbered from 1. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks.asList());
    }
}
