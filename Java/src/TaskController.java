import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class TaskController {
    // List of task types
    public static final List<String> transientTypes = Collections.unmodifiableList(Arrays.asList("Visit", "Shopping", "Appointment"));
    public static final List<String> recurringTypes = Collections.unmodifiableList(Arrays.asList("Class", "Study", "Sleep", "Exercise", "Work", "Meal"));
    public static final List<String> antiTypes = Collections.unmodifiableList(Arrays.asList("Cancellation"));

    TaskModel taskModel = new TaskModel();  // Have one single instance of TaskModel

    String[] months = {"",
            "January", "February", "March",
            "April", "May", "June",
            "July", "August", "September",
            "October", "November", "December"};
    int[] days = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    //first Scene
    @FXML
    private Button addTask;
    @FXML
    private Button editTask;
    @FXML
    private Button deleteTask;
    @FXML
    private GridPane calendarPane;
    @FXML
    private Label viewByLabel;



    //No Args constructor required
    public TaskController() {
    }

    //initialize
    @FXML
    public void initialize() {
        //grab month and year
        int month = java.time.LocalDate.now().getMonthValue();
        int year = java.time.LocalDate.now().getYear();

        //grab current date
        LocalDate firstOfTheMonth = java.time.LocalDate.of(year, month, 1);
        String str = firstOfTheMonth.toString().replace("-","");

        //grab the fist day of the month of the week
        int firstDay = TaskModel.getDayOfWeek(Integer.parseInt(str));
        firstDay+=7;

        //iterate through 2D flattened array to properly add date
        for(int i =firstDay; i <firstDay + days[month]; i++)
        {
            VBox temp = (VBox) calendarPane.getChildren().get(i);
            Text t = (Text) temp.getChildren().get(0);
            t.setText(Integer.toString(i-firstDay+1));
        }

        //set appropriate month
        viewByLabel.setText(months[month]);
    }

    //Main Scene Methods
    public void createTask() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("CreateTask.fxml"));
        Stage stage2 = new Stage();
        stage2.setScene(new Scene(root));
        stage2.show();
    }

    public void editTask(){

    }

    public void deleteTask(){

    }

    public void resetSchedule() {
    }

    /**
     * Read the schedule from a JSON file
     */

    public void loadSchedule() {
        // Possibly call resetSchedule() here if we're loading a new one?

        JSONParser parser = new JSONParser();

        // Get file name from user
        String fileName = getFileName();

        try {
            // Read and verify that file exists
            FileReader fileReader = new FileReader(fileName);

            // Convert json to array
            JSONArray jsonArray = (JSONArray) parser.parse(fileReader);

            // Add all tasks from file to TaskModel's lists, according for each task type
            jsonArray.forEach(obj -> {
                JSONObject task = (JSONObject) obj;

                // Determine what type of task this is through the 'type' attribute
                if (transientTypes.contains(task.get("Type"))) {
                    String name = (String) task.get("Name");
                    String type = (String) task.get("Type");
                    float startTime = (float) (long) task.get("StartTime");
                    float duration = (float) (double) task.get("Duration");
                    int date = (int) (long) task.get("Date");

                    taskModel.createTransientTask(name, type, startTime, duration, date);
                }

                else if (recurringTypes.contains(task.get("Type"))) {
                    String name = (String) task.get("Name");
                    String type = (String) task.get("Type");
                    float startTime = (float) (long) task.get("StartTime");  // Need to cast twice from Object -> long -> float for some reason
                    float duration = (float) (double) task.get("Duration");
                    int startDate = (int) (long) task.get("StartDate");
                    int endDate = (int) (long) task.get("EndDate");
                    int frequency = (int) (long) task.get("Frequency");

                    taskModel.createRecurringTask(name, type, startTime, duration, startDate, endDate, frequency);
                }

                else if (antiTypes.contains(task.get("Type"))) {
                    String name = (String) task.get("Name");
                    String type = (String) task.get("Type");
                    float startTime = (float) (long) task.get("StartTime");
                    float duration = (float) (double) task.get("Duration");
                    int date = (int) (long) task.get("Date");

                    taskModel.createAntiTask(name, type, startTime, duration, date);
                }

                else {
                    System.out.println("ERROR: Type may not be valid?");
                }
            });
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write schedule to file
     */
    public void exportSchedule() {
        // Get valid file name from user
    }

    public String getFileName() {
        // Implement code to grab string using GUI here?
        String userInput = "Set1.json"; //test value for now

        String prefix = "Java/src/";  // Can modify this depending on where json file is being stored
        return prefix + userInput;
    }
}
