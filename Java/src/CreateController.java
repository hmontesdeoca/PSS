import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class CreateController {
    //Task Model
    private TaskModel model;
    private Stage stage;
    //flags
    boolean validName = false;
    boolean startDateChosen = false;
    boolean startTimeChosen = false;
    boolean endTimeChosen = false;
    boolean freq = false;
    boolean endDateChosen = false;
    boolean radioSelected = false;
    //create Task Scene
    @FXML
    private TextField taskName;
    @FXML
    private DatePicker taskStartDate;
    @FXML
    private ComboBox<Float> startHour;
    @FXML
    private ComboBox startMin;
    @FXML
    private ComboBox startTimeCombo;
    @FXML
    private ComboBox<Float> endHour;
    @FXML
    private ComboBox endMin;
    @FXML
    private ComboBox endTimeCombo;
    @FXML
    private RadioButton transeintRadio;
    @FXML
    private RadioButton recurringDailyRadio;
    @FXML
    private RadioButton recurringWeeklyRadio;
    @FXML
    private DatePicker taskEndDate;
    @FXML
    private Button addTaskButton;
    @FXML
    private Label errorLabel;

    //no arg constructor
    public CreateController() {
    }

    //setter forscene
    public void giveStage(Stage stage){
        this.stage = stage;
    }

    //initialize function for adding items
    @FXML
    public void initialize() {
        startHour.getItems().addAll(1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f, 10f, 11f, 12f);
        endHour.getItems().addAll(1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f, 10f, 11f, 12f);

        startMin.getItems().addAll("00", "30");
        endMin.getItems().addAll("00", "30");

        startTimeCombo.getItems().addAll("AM", "PM");
        endTimeCombo.getItems().addAll("AM", "PM");
    }
    //setters and getters for task model
    public void setTaskModel(TaskModel model) {
        this.model = model;
    }

    public TaskModel getTaskModel() {
        return this.model;
    }

    public void checkName() {
        String current = taskName.getText();
        if (model.verifyName(current)) {
            errorLabel.setText("");
            validName = true;
        }
        else {
            errorLabel.setText("NAME TAKEN");
            validName = false;
        }
        enableButton();
    }

    //date picker frequency
    public void checkStartDate() {
        LocalDate date = taskStartDate.getValue();
        if (date != null)
            startDateChosen = true;
        enableButton();
    }

    //date picker frequency
    public void checkEndDate() {
        //if we have a task with frequency
        if (freq) {
            LocalDate date = taskEndDate.getValue();
            if (date != null)
                endDateChosen = true;
        }
        enableButton();
    }

    public void checkStartTime() {
        if (startTimeCombo.getValue() != null && startTimeCombo.getValue() != null
                && startMin.getValue() != null && startHour.getValue() != null)
            startTimeChosen = true;
        enableButton();
    }

    public void checkEndTime() {
        if (endTimeCombo.getValue() != null && endTimeCombo.getValue() != null
                && endMin.getValue() != null && endHour.getValue() != null)
            endTimeChosen = true;
        enableButton();
    }


    //check the frequency, assign it, and enable or disable the button
    public void checkFrequency() {
        if (recurringWeeklyRadio.isSelected() || recurringDailyRadio.isSelected()) {
            freq = true;
            taskEndDate.setDisable(false);
            radioSelected = true;
        } else if (transeintRadio.isSelected()) {
            taskEndDate.setDisable(true);
            radioSelected = true;
            freq = false;
        }
        enableButton();
    }

    public void enableButton() {
        if (validName && startDateChosen && startTimeChosen && endTimeChosen && radioSelected && !freq)
            addTaskButton.setDisable(false);
        else if (validName && startDateChosen && startTimeChosen && endTimeChosen && radioSelected && freq && endDateChosen)
            addTaskButton.setDisable(false);
        else
            addTaskButton.setDisable(true);
    }

    public void addTask() {

        //grab task name
        String name = taskName.getText();

        float startTime = (float) startHour.getValue();
        //if time is PM then add 12 for 24 hour format
        if (startTimeCombo.getValue().equals("PM") && startHour.getValue()!=12.0) {
            startTime += 12.0f;
        }
        //if it is at 30 minutes add 0.5 to float for format
        if (startMin.getValue().equals("30"))
            startTime += 0.5f;

        float endTime = (float) endHour.getValue();
        //if time is PM then add 12 for 24 hour format
        if (endTimeCombo.getValue().equals("PM") && endHour.getValue()!=12.0){
            endTime += 12.0f;
        }
        //if it is at 30 minutes add 0.5 to float for format
        if (endMin.getValue().equals("30"))
            endTime += 0.5f;

        //duration
        float duration = endTime - startTime;

        //start date of task
        int date = Integer.parseInt(taskStartDate.getValue().toString().replace("-", ""));

        //if this is a recurring task
        if (freq) {
            //create an end date for recurring tasks
            int endDate = Integer.parseInt(taskEndDate.getValue().toString().replace("-", ""));
            //if this is weekly
            if (recurringWeeklyRadio.isSelected()) {
                model.createRecurringTask(name, "other", startTime, duration, date, endDate, 7);
                System.out.println("STARTTIME: "+startTime);
                System.out.println("ENDTIME: " + endTime);
            }
            //if this is daily
            else
                model.createRecurringTask(name, "other", startTime, duration, date, endDate, 1);
                System.out.println("STARTTIME: "+startTime);
                System.out.println("ENDTIME: " + endTime);
        }
        //if the task is transient
        else {
            model.createTransientTask(name, "other", startTime, duration, date);
            System.out.println("STARTTIME: "+startTime);
            System.out.println("ENDTIME: " + endTime);
        }
        stage.close();
    }

}
