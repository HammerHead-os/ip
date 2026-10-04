package hedy.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

import hedy.task.Deadline;
import hedy.task.Event;
import hedy.task.Task;
import hedy.task.Todo;

/**
 * Saves the task list to a text file and loads it again when Hedy starts.
 * The path is relative to the folder where the program is run, so the same
 * code works on different computers and operating systems.
 */
public class Storage {
    private final Path filePath;

    /**
     * Stores tasks in {@code data/duke.txt} under the current working folder.
     * {@code Path.of("data", "duke.txt")} builds that path with the correct
     * separator for the operating system.
     */
    public Storage() {
        this.filePath = Path.of("data", "duke.txt");
    }

    /**
     * Reads the save file into a new list.
     * If the file or its folder does not exist yet, the list is empty.
     * Lines that are not in the save format are skipped, and Hedy tells the user
     * how many were skipped.
     */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }
        if (!Files.isRegularFile(filePath)) {
            System.out.println(" I could not read " + filePath + ", so I am starting with an empty list.");
            return tasks;
        }

        int skipped = 0;
        try (Scanner fileScanner = new Scanner(filePath)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (line.isBlank()) {
                    continue;
                }
                Task task = taskFromLine(line);
                if (task == null) {
                    skipped++;
                } else {
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            System.out.println(" I could not read " + filePath + ", so I am starting with an empty list.");
            return new ArrayList<>();
        }

        if (skipped > 0) {
            String lineWord = skipped == 1 ? "line" : "lines";
            System.out.println(" I skipped " + skipped + " " + lineWord + " in " + filePath
                    + " that were not in the expected format.");
        }
        return tasks;
    }

    /**
     * Writes every task to the save file, creating the data folder if needed.
     * Call this after each change to the list.
     */
    public void save(ArrayList<Task> tasks) {
        try {
            Files.createDirectories(filePath.getParent());
            StringBuilder content = new StringBuilder();
            for (Task task : tasks) {
                content.append(task.toFileFormat()).append(System.lineSeparator());
            }
            Files.writeString(filePath, content.toString());
        } catch (IOException e) {
            System.out.println(" I could not save your tasks to " + filePath + ".");
        }
    }

    /**
     * Builds one task from a saved line, or returns null if the line is unusable.
     * Expected forms:
     * {@code T | 1 | read book},
     * {@code D | 0 | return book | Sunday},
     * {@code E | 0 | meeting | Mon 2pm | 4pm}.
     * The middle field must be {@code 0} or {@code 1}.
     */
    private Task taskFromLine(String line) {
        String[] parts = line.split(" \\| ");
        if (parts.length < 3) {
            return null;
        }
        String type = parts[0];
        String doneFlag = parts[1];
        String description = parts[2];
        if ((!doneFlag.equals("0") && !doneFlag.equals("1")) || description.isEmpty()) {
            return null;
        }

        Task task;
        if (type.equals("T")) {
            task = new Todo(description);
        } else if (type.equals("D") && parts.length >= 4 && !parts[3].isEmpty()) {
            task = new Deadline(description, parts[3]);
        } else if (type.equals("E") && parts.length >= 5 && !parts[3].isEmpty() && !parts[4].isEmpty()) {
            task = new Event(description, parts[3], parts[4]);
        } else {
            return null;
        }

        if (doneFlag.equals("1")) {
            task.setDone(true);
        }
        return task;
    }
}
