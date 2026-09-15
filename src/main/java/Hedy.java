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
                try {
                    int index = Integer.parseInt(line.substring(5).trim()) - 1;
                    if (index >= 0 && index < taskCount) {
                        tasks[index].markAsDone();
                        System.out.println(" Nice! I've marked this task as done:");
                        System.out.println("   " + tasks[index]);
                    } else {
                        System.out.println(" Error: Task index out of bounds.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println(" Error: Please provide a valid task number to mark.");
                }
            } else if (line.startsWith("unmark ")) {
                try {
                    int index = Integer.parseInt(line.substring(7).trim()) - 1;
                    if (index >= 0 && index < taskCount) {
                        tasks[index].markAsNotDone();
                        System.out.println(" OK, I've marked this task as not done yet:");
                        System.out.println("   " + tasks[index]);
                    } else {
                        System.out.println(" Error: Task index out of bounds.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println(" Error: Please provide a valid task number to unmark.");
                }
            } else {
                tasks[taskCount] = new Task(line);
                taskCount++;
                System.out.println(" added: " + line);
            }
            System.out.println("____________________________________________________________");
        }
        scanner.close();
    }
}
