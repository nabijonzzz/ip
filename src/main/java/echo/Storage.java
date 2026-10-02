package echo;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads and writes the task list to a fixed file on disk, one task per line,
 * in the pipe-separated format produced by {@link Task#toSaveFormat()}.
 */
public class Storage {
    private final Path filePath;

    /**
     * @param filePath path (relative to the project root) of the save file, e.g. {@code "data/echo.txt"}.
     */
    public Storage(String filePath) {
        this.filePath = Path.of(filePath);
    }

    /**
     * Loads tasks from the save file. Returns an empty list if the file (or
     * its parent folder) does not exist yet -- e.g. the first run on a new
     * machine. A corrupted line (wrong field count, unknown type letter,
     * unparsable content) is skipped rather than failing the whole load.
     *
     * @return the tasks found in the save file, in file order.
     */
    public List<Task> load() {
        List<Task> loaded = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return loaded;
        }
        try {
            for (String line : Files.readAllLines(filePath)) {
                Task task = parseLine(line);
                if (task != null) {
                    loaded.add(task);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read " + filePath, e);
        }
        return loaded;
    }

    /**
     * Parses one save-file line into a task.
     *
     * @param line one line from the save file.
     * @return the parsed task, or {@code null} if the line is corrupted
     *         (including a date that is not in {@code yyyy-mm-dd} form).
     */
    private Task parseLine(String line) {
        String[] fields = line.split(" \\| ");
        if (fields.length < 3) {
            return null;
        }
        String type = fields[0];
        boolean isDone = fields[1].equals("1");
        String description = fields[2];
        Task task;
        try {
            switch (type) {
            case "T":
                task = new Todo(description);
                break;
            case "D":
                if (fields.length < 4) {
                    return null;
                }
                task = new Deadline(description, LocalDate.parse(fields[3]));
                break;
            case "E":
                if (fields.length < 5) {
                    return null;
                }
                task = new Event(description, fields[3], fields[4]);
                break;
            default:
                return null;
            }
        } catch (DateTimeParseException e) {
            return null;
        }
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Overwrites the save file with the given tasks, creating the parent
     * folder first if it does not exist yet.
     *
     * @param tasks the current task list, in the order to save them.
     */
    public void save(List<Task> tasks) {
        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }
            List<String> lines = new ArrayList<>();
            for (Task task : tasks) {
                lines.add(task.toSaveFormat());
            }
            Files.write(filePath, lines);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not save to " + filePath, e);
        }
    }
}
