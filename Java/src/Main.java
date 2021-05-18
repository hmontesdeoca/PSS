import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
        TaskModel t = new TaskModel();
        t.createRecurringTask("Workout", "other", 1.0f, 2.0f, 20210501, 20210529,1);
        t.createTransientTask("sleep", "other", 3.5f, 5.0f, 20210516);
    }

    public static void main(String[] args) {
        launch();
    }
}