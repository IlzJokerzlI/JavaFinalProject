package Page.MainPage;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


//Main Page of the class
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("mainFXML.fxml"));
        primaryStage.setTitle("Jialat Zipper");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(800);
        primaryStage.setOnCloseRequest((event -> {
            Platform.exit();
            System.exit(0);
        }));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
