package echo;

/**
 * Removes the task at a given list position.
 */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /**
     * @param taskNumber 1-based position shown by the list command.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws EchoException {
        checkTaskNumber(tasks, taskNumber);
        Task removed = tasks.remove(taskNumber - 1);
        ui.showTaskDeleted(removed, tasks.size());
        saveTasks(tasks, ui, storage);
    }
}
