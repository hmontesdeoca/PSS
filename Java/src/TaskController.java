import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class TaskController {
    public ArrayList<String> inCalendar = new ArrayList<>();

    // Have one single instance of TaskModel
    private TaskModel taskModel = new TaskModel();

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
        String str = firstOfTheMonth.toString().replace("-", "");

        //grab the fist day of the month of the week
        int firstDay = TaskModel.getDayOfWeek(Integer.parseInt(str));
        firstDay += 7;

        //iterate through 2D flattened array to properly add date
        for (int i = firstDay; i < firstDay + days[month]; i++) {
            VBox temp = (VBox) calendarPane.getChildren().get(i);
            Text t = (Text) temp.getChildren().get(0);
            t.setText(Integer.toString(i - firstDay + 1));
        }

        //set appropriate month
        viewByLabel.setText(months[month]);
    }

    //Main Scene Methods
    public void createTask() throws IOException {
        //use loader to load scene
        FXMLLoader load = new FXMLLoader(getClass().getResource("CreateTask.FXML"));
        Stage stage = new Stage();
        stage.setScene(new Scene(load.load()));

        //use loader to grab instance of controller
        CreateController temp = load.getController();

        //give the stage to the controller to close it when task is created
        temp.giveStage(stage);

        //set and get the task model to have the accurate data
        temp.setTaskModel(taskModel);
        taskModel = temp.getTaskModel();

        //show and wait and update the calendar to display the new tasks
        stage.showAndWait();
        updateCalendar();
    }

    public void editTask() {

    }

    public void deleteTask() throws IOException {
        //use loader to load scene
        FXMLLoader load = new FXMLLoader(getClass().getResource("delete.FXML"));
        Stage stage = new Stage();
        stage.setScene(new Scene(load.load()));
        //use loader to grab instance of controller
        DeleteController del = load.getController();

        //set and get the task model to have the accurate data
        del.setTaskModel(taskModel);
        del.populateList();
        taskModel = del.getTaskModel();
        stage.showAndWait();
        updateDelete();
        updateCalendar();
    }

    public void updateCalendar() {
        System.out.println("TASK MODEL SIZE: " + taskModel.getTaskList().size());
        for (int i = 0; i < taskModel.getTransientTasksList().size(); i++) {
            TransientTask currentTask = taskModel.getTransientTasksList().get(i);
            String date = String.valueOf(currentTask.getDate());

            for (int j = 7; j < calendarPane.getChildren().size() - 1; j++) {
                int vboxDay;
                VBox temp = (VBox) calendarPane.getChildren().get(j);
                try {
                    vboxDay = Integer.parseInt(((Text) temp.getChildren().get(0)).getText());
                } catch (NumberFormatException e) {
                    vboxDay = 0;
                }
                if (vboxDay == Integer.parseInt(date.substring(6)) && !inCalendar.contains(currentTask.getName())) {
                    temp.getChildren().add(new Text(currentTask.getName()));
                    inCalendar.add(currentTask.getName());
                }
            }
        }

        for (int i = 0; i < taskModel.getRecurringTaskList().size(); i++) {
            RecurringTask currentTask = taskModel.getRecurringTaskList().get(i);
            String date = String.valueOf(currentTask.getStartDate());

            for (int j = 7; j < calendarPane.getChildren().size() - 1; j++) {
                int vboxDay;
                VBox temp = (VBox) calendarPane.getChildren().get(j);
                try {
                    vboxDay = Integer.parseInt(((Text) temp.getChildren().get(0)).getText());
                } catch (NumberFormatException e) {
                    vboxDay = 0;
                }
                if (vboxDay == Integer.parseInt(date.substring(6)) && !inCalendar.contains(currentTask.getName())) {
                    temp.getChildren().add(new Text(currentTask.getName()));
                    inCalendar.add(currentTask.getName());
                }
            }
        }
    }

    public void updateDelete() {
        for (int i = 0; i < inCalendar.size(); i++) {
            boolean isDeleted = true;
            String currentTaskName = inCalendar.get(i);
            for (int j = 0; j < taskModel.getTaskList().size(); j++) {
                if (currentTaskName.equals(taskModel.getTaskList().get(i).getName()))
                    isDeleted = false;
            }
            if (isDeleted) {
                inCalendar.remove(currentTaskName);
                for (int k = 7; k < calendarPane.getChildren().size() - 1; k++) {
                    VBox temp = (VBox) calendarPane.getChildren().get(k);
                    for (int l = 1; l < temp.getChildren().size(); l++)
                        if (((Text) temp.getChildren().get(l)).getText().equals(currentTaskName))
                            temp.getChildren().remove(l);
                }
            }
        }
    }
}
