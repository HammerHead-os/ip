package hedy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

class HedyException extends Exception {
    public HedyException(String message) {
        super(message);
    }
}

class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    public void setDone(boolean done) {
        this.isDone = done;
    }

    public String toFileFormat() {
        return (isDone ? "1" : "0") + " | " + description;
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}

class Todo extends Task {
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toFileFormat() {
        return "T | " + super.toFileFormat();
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}

class Deadline extends Task {
    protected String by;

    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toFileFormat() {
        return "D | " + super.toFileFormat() + " | " + by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}

class Event extends Task {
    protected String start;
    protected String end;

    public Event(String description, String start, String end) {
        super(description);
        this.start = start;
        this.end = end;
    }

    @Override
    public String toFileFormat() {
        return "E | " + super.toFileFormat() + " | " + start + " | " + end;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + start + " to: " + end + ")";
    }
}

public class Hedy {
    private static final String LINE = "____________________________________________________________";
    private static final String FILE_PATH = "./data/duke.txt";
    private static ArrayList<Task> tasks = new ArrayList<>();

    public static void printLine() {
        System.out.println(LINE);
    }

    public static void main(String[] args) {
        loadTasks();
        Scanner scanner = new Scanner(System.in);

        printLine();
        System.out.println(" Hello! I'm Hedy");
        System.out.println(" What can I do for you?");
        printLine();

        while (true) {
            String input = scanner.nextLine().trim();
            
            try {
                printLine();
                if (input.equalsIgnoreCase("bye")) {
                    System.out.println(" Bye. Hope to see you again soon!");
                    printLine();
                    break;
                } else if (input.equalsIgnoreCase("list")) {
                    System.out.println(" Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println(" " + (i + 1) + "." + tasks.get(i));
                    }
                } else if (input.startsWith("mark ")) {
                    handleMark(input, true);
                } else if (input.startsWith("unmark ")) {
                    handleMark(input, false);
                } else if (input.startsWith("todo")) {
                    handleTodo(input);
                } else if (input.startsWith("deadline")) {
                    handleDeadline(input);
                } else if (input.startsWith("event")) {
                    handleEvent(input);
                } else if (input.startsWith("delete ")) {
                    handleDelete(input);
                } else if (input.isEmpty()) {
                    printLine();
                    continue;
                } else {
                    throw new HedyException("OOPS!!! Hedy is sorry, but she doesn't know what that means :-(");
                }
                printLine();
            } catch (HedyException e) {
                System.out.println(" " + e.getMessage());
                printLine();
            } catch (IndexOutOfBoundsException | NumberFormatException e) {
                System.out.println(" OOPS!!! Invalid task number or format provided.");
                printLine();
            }
        }
        scanner.close();
    }

    private static void loadTasks() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                return;
            }
            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(" \\| ");
                if (parts.length < 3) {
                    continue;
                }

                String type = parts[0];
                boolean isDone = parts[1].equals("1");
                String desc = parts[2];

                Task task = null;
                if (type.equals("T")) {
                    task = new Todo(desc);
                } else if (type.equals("D") && parts.length >= 4) {
                    task = new Deadline(desc, parts[3]);
                } else if (type.equals("E") && parts.length >= 5) {
                    task = new Event(desc, parts[3], parts[4]);
                }

                if (task != null) {
                    if (isDone) {
                        task.setDone(true);
                    }
                    tasks.add(task);
                }
            }
            fileScanner.close();
        } catch (IOException e) {
            // Ignore load errors
        }
    }

    private static void saveTasks() {
        try {
            File dir = new File("./data");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileWriter writer = new FileWriter(FILE_PATH);
            for (Task task : tasks) {
                writer.write(task.toFileFormat() + System.lineSeparator());
            }
            writer.close();
        } catch (IOException e) {
            System.out.println(" Error saving tasks to disk.");
        }
    }

    private static void handleTodo(String input) throws HedyException {
        if (input.length() <= 4 || input.substring(4).trim().isEmpty()) {
            throw new HedyException("OOPS!!! Hedy says the description of a todo cannot be empty.");
        }
        String description = input.substring(4).trim();
        Task newTask = new Todo(description);
        tasks.add(newTask);
        saveTasks();
        printTaskAdded(newTask);
    }

    private static void handleDeadline(String input) throws HedyException {
        if (input.length() <= 8 || input.substring(8).trim().isEmpty()) {
            throw new HedyException("OOPS!!! Hedy says the description of a deadline cannot be empty.");
        }
        String content = input.substring(8).trim();
        String[] parts = content.split(" /by ");
        if (parts.length < 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
            throw new HedyException("OOPS!!! Invalid deadline format. Use: deadline <desc> /by <time>");
        }
        Task newTask = new Deadline(parts[0].trim(), parts[1].trim());
        tasks.add(newTask);
        saveTasks();
        printTaskAdded(newTask);
    }

    private static void handleEvent(String input) throws HedyException {
        if (input.length() <= 5 || input.substring(5).trim().isEmpty()) {
            throw new HedyException("OOPS!!! Hedy says the description of an event cannot be empty.");
        }
        String content = input.substring(5).trim();
        String[] parts = content.split(" /from ");
        if (parts.length < 2 || parts[0].isEmpty()) {
            throw new HedyException("OOPS!!! Invalid event format. Use: event <desc> /from <start> /to <end>");
        }
        String desc = parts[0].trim();
        String[] timeParts = parts[1].split(" /to ");
        if (timeParts.length < 2 || timeParts[0].isEmpty() || timeParts[1].isEmpty()) {
            throw new HedyException("OOPS!!! Invalid event time range. Use /from <start> /to <end>");
        }
        Task newTask = new Event(desc, timeParts[0].trim(), timeParts[1].trim());
        tasks.add(newTask);
        saveTasks();
        printTaskAdded(newTask);
    }

    private static void handleDelete(String input) throws HedyException {
        String[] parts = input.split(" ");
        if (parts.length < 2) {
            throw new HedyException("OOPS!!! Please specify which task number you want to delete.");
        }
        
        int index;
        try {
            index = Integer.parseInt(parts[1]) - 1;
        } catch (NumberFormatException e) {
            throw new HedyException("OOPS!!! That is not a valid task number.");
        }

        if (index < 0 || index >= tasks.size()) {
            throw new HedyException("OOPS!!! That task number does not exist in your list.");
        }

        Task removedTask = tasks.remove(index);
        saveTasks();
        System.out.println(" Noted. Hedy has removed this task:");
        System.out.println("   " + removedTask);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
    }

    private static void handleMark(String input, boolean isDone) throws HedyException {
        String[] parts = input.split(" ");
        if (parts.length < 2) {
            throw new HedyException("OOPS!!! Please specify the task number to mark/unmark.");
        }
        int index;
        try {
            index = Integer.parseInt(parts[1]) - 1;
        } catch (NumberFormatException e) {
            throw new HedyException("OOPS!!! That is not a valid task number.");
        }
        if (index < 0 || index >= tasks.size()) {
            throw new HedyException("OOPS!!! That task number does not exist.");
        }
        tasks.get(index).setDone(isDone);
        saveTasks();
        if (isDone) {
            System.out.println(" Nice! Hedy has marked this task as done:");
        } else {
            System.out.println(" OK, Hedy has marked this task as not done yet:");
        }
        System.out.println("   " + tasks.get(index));
    }

    private static void printTaskAdded(Task task) {
        System.out.println(" Got it. Hedy has added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
    }
}
