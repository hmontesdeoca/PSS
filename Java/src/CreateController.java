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

public class CreateController {
    //Task Model
    TaskModel model = new TaskModel();

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
    private ComboBox startHour;
    @FXML
    private ComboBox startMin;
    @FXML
    private ComboBox startTimeCombo;
    @FXML
    private ComboBox endHour;
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

    //no arg constructor
    public CreateController() {

    }

    @FXML
    public void initialize() {
        startHour.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
        endHour.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);

        startMin.getItems().addAll("00", "30");
        endMin.getItems().addAll("00", "30");

        startTimeCombo.getItems().addAll("AM", "PM");
        endTimeCombo.getItems().addAll("AM", "PM");
    }

    public void checkName() {
        String current = taskName.getText();
        if (model.verifyName(current))
            validName = true;
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
            freq=false;
        }
        enableButton();
    }

    public void enableButton() {
        if(validName && startDateChosen && startTimeChosen && endTimeChosen && radioSelected && !freq)
            addTaskButton.setDisable(false);
        else if(validName && startDateChosen && startTimeChosen && endTimeChosen && radioSelected && freq && endDateChosen)
            addTaskButton.setDisable(false);
        else
            addTaskButton.setDisable(true);
    }

    public void addTask(){

    }
}
