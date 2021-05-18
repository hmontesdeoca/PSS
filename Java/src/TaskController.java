import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;


public class TaskController {
    String[] months = {"",
            "January", "February", "March",
            "April", "May", "June",
            "July", "August", "September",
            "October", "November", "December"};
    int[] days = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
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
        int month = java.time.LocalDate.now().getMonthValue();
        int year = java.time.LocalDate.now().getYear();

        LocalDate firstOfTheMonth = java.time.LocalDate.of(year, month, 1);
        String str = firstOfTheMonth.toString().replace("-","");

        int firstDay = TaskModel.getDayOfWeek(Integer.parseInt(str));
        firstDay+=7;

        for(int i =firstDay; i <firstDay + days[month]; i++)
        {
            VBox temp = (VBox) calendarPane.getChildren().get(i);
            Text t = (Text) temp.getChildren().get(0);
            t.setText(Integer.toString(i-firstDay+1));
        }
        System.out.println(months[month]);
        viewByLabel.setText(months[month]);
    }

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
}
