import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class HelloFX extends Application {

    @Override
    public void start(Stage stage) {
        /*Menu set-up*/

        //Menu Sub-Items
        MenuItem save = new MenuItem("Save");
        MenuItem open = new MenuItem("Open");
        MenuItem exit = new MenuItem("Exit");

        //Menu Item
        Menu fileMenu = new Menu("File");
        fileMenu.getItems().addAll(save, new SeparatorMenuItem(),open,new SeparatorMenuItem(),exit);

        //Menu Bar
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(fileMenu);

        //BorderPane set-up
        BorderPane mainBorderPane = new BorderPane();
        mainBorderPane.setPadding(new Insets(5));
        mainBorderPane.setTop(menuBar);

        //Scene Setup
        Scene scene = new Scene(mainBorderPane, 840, 580);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}