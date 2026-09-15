import java.util.Scanner;

public class Hedy {
    public static void main(String[] args) {
        System.out.println("____________________________________________________________");
        System.out.println("Hello! I'm Hedy.");
        System.out.println("What can I do for you?");
        System.out.println("____________________________________________________________");

        Scanner scanner = new Scanner(System.in);
        String[] tasks = new String[100];
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
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(" " + (i + 1) + ". " + tasks[i]);
                }
            } else {
                tasks[taskCount] = line;
                taskCount++;
                System.out.println(" added: " + line);
            }
            System.out.println("____________________________________________________________");
        }
        scanner.close();
    }
}
