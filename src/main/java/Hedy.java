import java.util.Scanner;

public class Hedy {
    public static void main(String[] args) {
        String logo = " _    _          _       \n"
                    + "| |  | |        | |      \n"
                    + "| |__| |   ___  | | _  _ \n"
                    + "|  __  |  / _ \\ | || |/ / \n"
                    + "| |  | | |  __/ | | |   <  \n"
                    + "|_|  |_|  \\___| |_| |_|\\_\\ \n";
        System.out.println("Hello from\n" + logo);
        System.out.println("____________________________________________________________");
        System.out.println("Hello! I'm Hedy.");
        System.out.println("What can I do for you?");
        System.out.println("____________________________________________________________");

        Scanner scanner = new Scanner(System.in);
        String line;

        while (true) {
            line = scanner.nextLine().trim();
            System.out.println("____________________________________________________________");
            if (line.equalsIgnoreCase("bye")) {
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println("____________________________________________________________");
                break;
            }
            System.out.println(" " + line);
            System.out.println("____________________________________________________________");
        }
        scanner.close();
    }
}
