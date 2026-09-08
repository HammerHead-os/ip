/**
 * Entry point for the Hedy personal assistant chatbot.
 * Named after Hedy Lamarr; keeps a professional, student-friendly tone.
 */
public class Hedy {
    public static void main(String[] args) {
        String line = "____________________________________________________________";
        String banner = " _   _          _       \n"
                + "| | | | ___  __| |_   _ \n"
                + "| |_| |/ _ \\/ _` | | | |\n"
                + "|  _  |  __/ (_| | |_| |\n"
                + "|_| |_|\\___|\\__,_|\\__, |\n"
                + "                  |___/ \n";

        System.out.println(line);
        System.out.print(banner);
        System.out.println("Hello! I'm Hedy.");
        System.out.println("What can I do for you?");
        System.out.println(line);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(line);
    }
}
