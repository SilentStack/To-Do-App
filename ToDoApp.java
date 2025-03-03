import java.io.*; // Importing input-output classes for file handling
import java.sql.*; // Importing SQL classes for database connectivity
import java.util.*; // Importing utility classes like Scanner and ArrayList

public class ToDoApp {
    // Constants for file name and database credentials
    static final String FILE_NAME = "tasks.txt";
    static final String DB_URL = "jdbc:mysql://localhost:3306/todo_db";
    static final String USER = "root";
    static final String PASSWORD = ""; //Put ur SQL password if exists
    
    // List to store tasks in memory
    static ArrayList<Task> tasks = new ArrayList<>();
    static int taskID = 1; // Task ID counter

    public static void main(String[] args) {
        // Load tasks from file and database when program starts
        loadTasksFromFile();
        loadTasksFromDatabase();

        Scanner sc = new Scanner(System.in); // Scanner for user input
        
        while (true) { // Infinite loop for menu options
            System.out.println("\n\ud83d\udccc To-Do List Menu:");
            System.out.println("1. Add Task");
            System.out.println("2. View Tasks");
            System.out.println("3. Mark Task as Completed");
            System.out.println("4. Remove Task");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt(); // Read user choice
            sc.nextLine(); // Consume newline character
        
            switch (choice) { // Menu selection
                case 1: 
                    addTask(sc);
                    break;
                case 2: 
                    viewTasks();
                    break;
                case 3:
                    completeTask(sc);
                    break;
                case 4: 
                    removeTask(sc);
                    break;    
                case 5: 
                    saveTaskToFile(); // Save tasks before exiting
                    System.out.println("Tasks saved!...");
                    return; // Exit the program
                default:
                    System.out.println("Invalid Choice");
                    break;
            }
        }
    }
    
    // Load tasks from file
    public static void loadTasksFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) { // Read each line from file
                Task task = Task.fromString(line); // Convert line to Task object
                tasks.add(task); // Add task to list
                taskID = Math.max(taskID, taskID + 1); // Update task ID counter                
            }
        } catch (IOException e) {
            System.out.println("No previous tasks found in file. " + e.getMessage());
        }
    }
    
    // Load tasks from database
    public static void loadTasksFromDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM tasks")) { // Execute query
            while (rs.next()) { // Loop through each row in database
                Task task = new Task(rs.getInt("id"), rs.getString("description"), rs.getBoolean("isCompleted"));
                tasks.add(task); // Add task to list
                taskID = Math.max(taskID, taskID + 1); // Update task ID counter
            }
        } catch (SQLException e) {
            System.out.println("\u26a0 Database connection error: " + e.getMessage());
        }
    }
    
    // Save tasks to file
    public static void saveTaskToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Task task : tasks) {
                writer.write(task.toString()); // Write task to file
                writer.newLine(); // New line for next task
            }
        } catch (IOException e) {
            System.out.println("Error saving tasks to the file");
        }
    }
    
    // Add a new task
    public static void addTask(Scanner sc) {
        System.out.print("Enter the description: ");
        String description = sc.nextLine(); // Read task description
        Task newTask = new Task(taskID++, description, false); // Create new task object
        tasks.add(newTask); // Add task to list

        // Save task to database
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO tasks (description, isCompleted) VALUES (?, false)")) {
            stmt.setString(1, description);
            stmt.executeUpdate(); // Execute insert query
        } catch (SQLException e) {
            System.out.println("Error saving task to DB: " + e.getMessage());
        }
        System.out.println("Task added");
    }
    
    // View all tasks
    public static void viewTasks() {
        if (tasks.isEmpty()) {
            System.out.println("\ud83d\udc6d No tasks found!");
            return;
        }
        System.out.println("\n\ud83d\udcda Your Tasks:");
        for (Task task : tasks) {
            System.out.println(task.id + ". " + task.description + " [" + (task.isCompleted ? "Completed" : "Pending") + "]");
        }
    }
    
    // Mark task as completed
    public static void completeTask(Scanner scanner) {
        viewTasks(); // Display tasks
        System.out.print("Enter task number to mark as completed: ");
        int id = scanner.nextInt();
        boolean found = false;

        for (Task task : tasks) {
            if (task.id == id) {
                task.isCompleted = true; // Mark as completed
                found = true;
                break;
            }
        }

        if (found) {
            // Update task in database
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement("UPDATE tasks SET isCompleted = true WHERE id = ?")) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println("❌ Error updating task in database: " + e.getMessage());
            }
            System.out.println("✅ Task marked as completed!");
        } else {
            System.out.println("❌ Task not found!");
        }
    }
    
    // Remove a task
    public static void removeTask(Scanner scanner) {
        viewTasks(); // Display tasks
        System.out.print("Enter task number to remove: ");
        int id = scanner.nextInt();
        boolean removed = tasks.removeIf(task -> task.id == id); // Remove task from list

        if (removed) {
            // Remove from database
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM tasks WHERE id = ?")) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println("❌ Error deleting task from database: " + e.getMessage());
            }
            System.out.println("✅ Task removed!");
        } else {
            System.out.println("❌ Task not found!");
        }
    }
}
