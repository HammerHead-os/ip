package hedy.task;

/**
 * A task that must be finished by a particular time.
 */
public class Deadline extends Task {
    protected String by;

    /**
     * Creates a deadline.
     *
     * @param description what needs to be done
     * @param by when it is due, stored as the user typed it
     */
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
