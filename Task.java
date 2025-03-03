// Defining the Task class to represent a to-do item
public class Task {
    // Attributes of the Task class
    int id;                 // Unique ID for each task
    String description;     // Task description
    boolean isCompleted;    // Status of the task (true if completed, false otherwise)

    // Constructor to initialize a task object with an ID, description, and completion status
    public Task(int id, String description, boolean isCompleted) {
        this.id = id; 
        this.description = description;
        this.isCompleted = isCompleted; 
    }

    // Override the toString() method to format task data for file/database storage
    @Override
    public String toString() {
        return id + "," + description + "," + isCompleted; // Returns task details as a comma-separated string
    }

    // Static method to create a Task object from a stored string (used when reading from file/database)
    public static Task fromString(String line) {
        String[] parts = line.split(","); // Splits the stored string into an array using commas
        return new Task(Integer.parseInt(parts[0]), parts[1], Boolean.parseBoolean(parts[2])); 
        // Converts string data back to appropriate types and returns a new Task object
    }
}
