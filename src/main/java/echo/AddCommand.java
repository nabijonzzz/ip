package echo;

/**
 * Adds a task (to-do, deadline or event) to the task list.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * @param task the already-parsed task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        tasks.add(task);
        ui.showTaskAdded(task, tasks.size());
        saveTasks(tasks, ui, storage);
    }
}
