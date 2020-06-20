package Page.MainPage;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * Main Page of the class
 */
public class Main extends Application {


    /**
     * start Method
     * The main entry point for all JavaFX applications, implements start method from Application Interface. The start method is called after the init method has returned, and after the system is ready for the application to begin running.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("mainFXML.fxml")); //Loads an object hierarchy from a FXML document.
        primaryStage.setTitle("Jialat Zipper"); //Sets title
        primaryStage.setScene(new Scene(root)); //Sets scene
        primaryStage.setMinHeight(600); //Sets minimum window height
        primaryStage.setMinWidth(800); //Sets minimum window width

        //Performs action when app closes
        primaryStage.setOnCloseRequest((event -> {
            Platform.exit(); //Exit app
            System.exit(0); //Exist system
        }));

        primaryStage.show(); //Shows stage
    }


    /**
     * main entry point
     * @param args: arguments
     */
    public static void main(String[] args) {
        launch(args); //launch
    }
}
