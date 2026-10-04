package hedy;

import java.util.ArrayList;
import java.util.Scanner;

import hedy.exception.HedyException;
import hedy.storage.Storage;
import hedy.task.Deadline;
import hedy.task.Event;
import hedy.task.Task;
import hedy.task.Todo;

/**
 * Hedy is a command-line chatbot that tracks todos, deadlines, and events.
 * Commands that are incomplete or unknown are reported with a specific message
 * instead of crashing the program.
 */
public class Hedy {
    private static final String LINE = "____________________________________________________________";
    private static final Storage storage = new Storage();
    private static ArrayList<Task> tasks = new ArrayList<>();

    public static void main(String[] args) {
        tasks = storage.load();
        Scanner scanner = new Scanner(System.in);

        printLine();
        System.out.println(" Hello! I'm Hedy");
        System.out.println(" What can I do for you?");
        printLine();

        while (true) {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                continue;
            }

            try {
                printLine();
                if (!handleCommand(input)) {
                    printLine();
                    break;
                }
                printLine();
            } catch (HedyException e) {
                System.out.println(" " + e.getMessage());
                printLine();
            }
        }
        scanner.close();
    }

    /**
     * Runs one user command.
     *
     * @param input the full line the user typed
     * @return false when the user says bye, so the program can exit
     * @throws HedyException if the command is unknown or incomplete
     */
    private static boolean handleCommand(String input) throws HedyException {
        String command = firstWord(input);
        String arguments = argumentsOf(input);

        switch (command) {
        case "bye":
            System.out.println(" Bye. Hope to see you again soon!");
            return false;
        case "list":
            printTaskList();
            return true;
        case "mark":
            handleMark(arguments, true);
            return true;
        case "unmark":
            handleMark(arguments, false);
            return true;
        case "todo":
            handleTodo(arguments);
            return true;
        case "deadline":
            handleDeadline(arguments);
            return true;
        case "event":
            handleEvent(arguments);
            return true;
        case "delete":
            handleDelete(arguments);
            return true;
        default:
            throw new HedyException("OOPS!!! I don't know what that means. "
                    + "Try list, todo, deadline, event, mark, unmark, delete, or bye.");
        }
    }

    private static void printTaskList() {
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Adds a todo. The description after {@code todo} must not be empty.
     */
    private static void handleTodo(String arguments) throws HedyException {
        if (arguments.isEmpty()) {
            throw new HedyException("OOPS!!! The description of a todo cannot be empty. "
                    + "Try: todo read book");
        }
        Task newTask = new Todo(arguments);
        tasks.add(newTask);
        storage.save(tasks);
        printTaskAdded(newTask);
    }

    /**
     * Adds a deadline. Expected form: {@code deadline <description> /by <time>}.
     */
    private static void handleDeadline(String arguments) throws HedyException {
        if (arguments.isEmpty()) {
            throw new HedyException("OOPS!!! The description of a deadline cannot be empty. "
                    + "Try: deadline return book /by Sunday");
        }
        String[] parts = arguments.split("\\s*/by\\s*", 2);
        if (parts.length < 2) {
            throw new HedyException("OOPS!!! A deadline needs a /by time. "
                    + "Try: deadline return book /by Sunday");
        }
        String description = parts[0].trim();
        String by = parts[1].trim();
        if (description.isEmpty()) {
            throw new HedyException("OOPS!!! The description before /by cannot be empty. "
                    + "Try: deadline return book /by Sunday");
        }
        if (by.isEmpty()) {
            throw new HedyException("OOPS!!! The time after /by cannot be empty. "
                    + "Try: deadline return book /by Sunday");
        }
        Task newTask = new Deadline(description, by);
        tasks.add(newTask);
        storage.save(tasks);
        printTaskAdded(newTask);
    }

    /**
     * Adds an event. Expected form: {@code event <description> /from <start> /to <end>}.
     */
    private static void handleEvent(String arguments) throws HedyException {
        if (arguments.isEmpty()) {
            throw new HedyException("OOPS!!! The description of an event cannot be empty. "
                    + "Try: event project meeting /from Mon 2pm /to 4pm");
        }
        String[] fromParts = arguments.split("\\s*/from\\s*", 2);
        if (fromParts.length < 2) {
            throw new HedyException("OOPS!!! An event needs a /from start and a /to end. "
                    + "Try: event project meeting /from Mon 2pm /to 4pm");
        }
        String description = fromParts[0].trim();
        String[] timeParts = fromParts[1].split("\\s*/to\\s*", 2);
        if (timeParts.length < 2) {
            throw new HedyException("OOPS!!! An event needs a /to end time. "
                    + "Try: event project meeting /from Mon 2pm /to 4pm");
        }
        String start = timeParts[0].trim();
        String end = timeParts[1].trim();
        if (description.isEmpty() || start.isEmpty() || end.isEmpty()) {
            throw new HedyException("OOPS!!! The description, start, and end of an event cannot be empty. "
                    + "Try: event project meeting /from Mon 2pm /to 4pm");
        }
        Task newTask = new Event(description, start, end);
        tasks.add(newTask);
        storage.save(tasks);
        printTaskAdded(newTask);
    }

    /**
     * Deletes the task at the given 1-based index.
     */
    private static void handleDelete(String arguments) throws HedyException {
        int index = parseTaskIndex(arguments, "delete");
        Task removedTask = tasks.remove(index);
        storage.save(tasks);
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + removedTask);
        System.out.println(" Now you have " + taskCountText() + " in the list.");
    }

    /**
     * Marks or unmarks the task at the given 1-based index.
     */
    private static void handleMark(String arguments, boolean isDone) throws HedyException {
        String commandName = isDone ? "mark" : "unmark";
        int index = parseTaskIndex(arguments, commandName);
        tasks.get(index).setDone(isDone);
        storage.save(tasks);
        if (isDone) {
            System.out.println(" Nice! I've marked this task as done:");
        } else {
            System.out.println(" OK, I've marked this task as not done yet:");
        }
        System.out.println("   " + tasks.get(index));
    }

    /**
     * Converts a user-typed task number into a list index.
     * Task numbers shown by {@code list} start at 1.
     *
     * @param arguments the text after the command word
     * @param commandName the command, used to suggest a correction
     */
    private static int parseTaskIndex(String arguments, String commandName) throws HedyException {
        if (arguments.isEmpty()) {
            throw new HedyException("OOPS!!! Please give a task number. "
                    + "Try: " + commandName + " 1");
        }
        String numberText = arguments.split("\\s+")[0];
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException e) {
            throw new HedyException("OOPS!!! \"" + numberText + "\" is not a task number. "
                    + "Try: " + commandName + " 1");
        }
        if (tasks.isEmpty()) {
            throw new HedyException("OOPS!!! Your list is empty, so there is no task " + taskNumber + " to "
                    + commandName + ".");
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new HedyException("OOPS!!! There is no task " + taskNumber + ". "
                    + "Use list to see tasks numbered 1 to " + tasks.size() + ".");
        }
        return taskNumber - 1;
    }

    private static void printTaskAdded(Task task) {
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCountText() + " in the list.");
    }

    /** Returns {@code "1 task"} or {@code "2 tasks"}, matching the current list size. */
    private static String taskCountText() {
        int count = tasks.size();
        return count + (count == 1 ? " task" : " tasks");
    }

    /** Prints the horizontal line used around every reply. */
    public static void printLine() {
        System.out.println(LINE);
    }

    /**
     * Returns the first word of a command, in lower case.
     * {@code "Todo read book"} becomes {@code "todo"}.
     */
    private static String firstWord(String input) {
        int space = input.indexOf(' ');
        String word = space == -1 ? input : input.substring(0, space);
        return word.toLowerCase();
    }

    /**
     * Returns the text after the command word, with surrounding spaces removed.
     */
    private static String argumentsOf(String input) {
        int space = input.indexOf(' ');
        if (space == -1) {
            return "";
        }
        return input.substring(space + 1).trim();
    }
}
