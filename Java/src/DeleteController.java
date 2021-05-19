import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class DeleteController
{
    //task model
    private TaskModel model = new TaskModel();

    private String selectedItem;
    private int indexOfListView;
    //FXML GUI's
    @FXML
    ListView<String> deleteListView;
    @FXML
    Button deleteButton;

    public TaskModel getTaskModel(){
       return this.model;
    }
    public void setTaskModel(TaskModel model){
        this.model = model;
    }

    public DeleteController(){
    }

    @FXML
    public void initialize(){
        deleteListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                selectedItem=newValue;
                indexOfListView=deleteListView.getItems().indexOf(selectedItem);
            }
        });

    }

    public void populateList(){
        for(int i =0; i<model.getTaskList().size(); i++){
            deleteListView.getItems().add(model.getTaskList().get(i).getName());
        }
    }

    public void deleteTask(){
        for(int i =0; i < model.getTaskList().size();i++){
            if(selectedItem.equals(model.getTaskList().get(i).getName()))
                model.getTaskList().remove(i);
        }
        for(int i =0; i < model.getRecurringTaskList().size();i++){
            if(selectedItem.equals(model.getRecurringTaskList().get(i).getName()))
                model.getRecurringTaskList().remove(i);
        }
        for(int i =0; i < model.getTransientTasksList().size();i++){
            if(selectedItem.equals(model.getTransientTasksList().get(i).getName()))
                model.getTransientTasksList().remove(i);
        }
        deleteListView.getItems().remove(indexOfListView);
    }
}
