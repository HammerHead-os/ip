import java.util.Scanner;

import java.util.Scanner;

public class Hedy {
    public static void main(String[] args) {
        System.out.println("____________________________________________________________");
        System.out.println("Hello! I'm Hedy.");
        System.out.println("What can I do for you?");
        System.out.println("____________________________________________________________");

        Scanner scanner = new Scanner(System.in);
        Task[] tasks = new Task[100];
        int taskCount = 0;
        String line;

        while (true) {
            line = scanner.nextLine().trim();
            System.out.println("____________________________________________________________");

            if (line.equalsIgnoreCase("bye")) {
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println("____________________________________________________________");
                break;
            } else if (line.equalsIgnoreCase("list")) {
                System.out.println(" Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(" " + (i + 1) + "." + tasks[i]);
                }
            } else if (line.startsWith("mark ")) {
                int index = Integer.parseInt(line.substring(5).trim()) - 1;
                tasks[index].markAsDone();
                System.out.println(" Nice! I've marked this task as done:");
                System.out.println("   " + tasks[index]);
            } else if (line.startsWith("unmark ")) {
                int index = Integer.parseInt(line.substring(7).trim()) - 1;
                tasks[index].markAsNotDone();
                System.out.println(" OK, I've marked this task as not done yet:");
                System.out.println("   " + tasks[index]);
            } else if (line.startsWith("todo ")) {
                String desc = line.substring(5).trim();
                tasks[taskCount] = new Todo(desc);
                taskCount++;
                printTaskAdded(tasks[taskCount - 1], taskCount);
            } else if (line.startsWith("deadline ")) {
                String[] parts = line.substring(9).split(" /by ");
                tasks[taskCount] = new Deadline(parts[0].trim(), parts[1].trim());
                taskCount++;
                printTaskAdded(tasks[taskCount - 1], taskCount);
            } else if (line.startsWith("event ")) {
                String[] parts = line.substring(6).split(" /from ");
                String[] timeParts = parts[1].split(" /to ");
                tasks[taskCount] = new Event(parts[0].trim(), timeParts[0].trim(), timeParts[1].trim());
                taskCount++;
                printTaskAdded(tasks[taskCount - 1], taskCount);
            } else {
                System.out.println(" Unknown command.");
            }
            System.out.println("____________________________________________________________");
        }
        scanner.close();
    }

    private static void printTaskAdded(Task task, int count) {
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + count + " tasks in the list.");
    }
}
