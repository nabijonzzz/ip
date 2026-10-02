package echo;

/**
 * Marks the task at a given list position as done.
 */
public class MarkCommand extends Command {
    private final int taskNumber;

    /**
     * @param taskNumber 1-based position shown by the list command.
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws EchoException {
        checkTaskNumber(tasks, taskNumber);
        Task task = tasks.get(taskNumber - 1);
        task.markAsDone();
        ui.showTaskStatusChanged(task, true);
        saveTasks(tasks, ui, storage);
    }
}
