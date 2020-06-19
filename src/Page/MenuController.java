package Page;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;


/** MenuController Interface
 *  An interface implemented by its kind such as the ZipSubPage and UnzipSubPage which will implements all the basic methods needed by the page.
 * **/
public abstract class MenuController implements Initializable {

    @FXML
    public TextField zipPathField = new TextField();
    public Button zipPathChooserButton = new Button();
    public Label pathErrorLabel = new Label();
    public TextField fileNameField = new TextField();
    public Label fileNameErrorLabel = new Label();
    public CheckBox encryptCheckBox = new CheckBox();
    public PasswordField passwordField = new PasswordField();
    public Label passwordError = new Label();
    public ProgressBar progressBar = new ProgressBar();
    public Button executeZipButton = new Button();
    public Button cancelButton = new Button();

    protected Stage stage;
    protected Map<String, File> selectedChildrenPaths;
    protected boolean isPathValid = true;
    protected boolean isNameValid = false;
    protected boolean isPasswordValid = false;
    protected String currentPath;

    protected double xOffSet = 0;
    protected double yOffset = 0;

    protected Dialogs dialogs = new Dialogs();


    @Override
    public void initialize(URL location, ResourceBundle resources) {}


    public void initData(Stage stage, String currentPath, Map<String, File> selectedChildrenPaths) {
        this.stage = stage;
        this.currentPath = currentPath;
        this.selectedChildrenPaths = selectedChildrenPaths;

        this.zipPathField.setText(this.currentPath);
    }


    public void onWindowDragged() {
        this.stage.getScene().setOnMousePressed((event -> {
            this.xOffSet = event.getSceneX();
            this.yOffset = event.getSceneY();
        }));

        this.stage.getScene().setOnMouseDragged((event -> {
            this.stage.setX(event.getScreenX() - this.xOffSet);
            this.stage.setY(event.getScreenY() - this.yOffset);
        }));
    }


    public void freezeWindowProperties() {
        this.zipPathField.setDisable(true);
        this.fileNameField.setDisable(true);
        this.encryptCheckBox.setDisable(true);
        this.passwordField.setDisable(true);
        this.executeZipButton.setDisable(true);
        this.cancelButton.setDisable(true);
        this.zipPathChooserButton.setDisable(true);
    }


    public void onExecuteComplete() {
        this.cancelButton.setDisable(false);
        this.cancelButton.setText("Done");
    }


    public void execute() {}


    public void setExecuteStatus() {}


    public void checkNameExist() {}


    /**
     * Called when any change is done on zipPathField.
     * As soon as a key is released, an event will be triggered, which calls upon this function to check if the content (path text) in zipPathField is a directory. The function will set isPathValid to false and pathErrorLabel to visible in case the directory path is not valid, else it will set isPathValid to true, update the currentPath and call checkFileNameExist to ensure the file name in fileNameField is not equal to any files in the directory path. This function is responsible to enable the executeZipButton by calling setExecuteZipStatus function to refresh the button as long as the final path in zipPathField is valid.
     * **/
    public void onPathFieldEdited() {
        if (!new File(this.zipPathField.getText()).isDirectory()) {
            this.isPathValid = false;
        } else {
            this.isPathValid = true;
            this.currentPath = this.zipPathField.getText();
            this.checkNameExist();
        }

        this.pathErrorLabel.setVisible(!this.isPathValid);
        this.setExecuteStatus();
    }


    /**
     * The function is called upon the completion of zipPathField editing which is by pressing enter key. It sets the zipPathField to the currentPath (temporarily storing last valid path) if the input path is not valid. Then it will call setExecuteZipStatus to updates the availability of executeZipButton.
     * **/
    public void onPathFieldComplete() {
        if (!this.isPathValid) {
            this.zipPathField.setText(this.currentPath);
        }
        this.setExecuteStatus();
    }


    /**
     * The function is triggered when zipPathChooserButton is pressed. It shows a DirectoryChooser dialog, allowing user to choose the directory location for storing zip file. The function updates zipPathField, checks the validity of the selected directory path by calling onZipPathFieldEdited, and updates the availability of executeZipButton if a directory is chosen from the dialog. On the other hand, the function does nothing when the operation is canceled.
     * **/
    public void onChoosePath() {
        DirectoryChooser dc = new DirectoryChooser();
        File selectedDir = dc.showDialog(this.stage);

        if (selectedDir != null) {
            this.zipPathField.setText(selectedDir.getPath());
            this.currentPath = this.zipPathField.getText();
            this.checkNameExist();
            this.setExecuteStatus();
        }
    }


    public void onNameFieldEdited() {
        if (this.fileNameField.getText().isEmpty()) {
            this.isNameValid = false;
            this.fileNameErrorLabel.setText("Name field must be filled");
        } else {
            this.checkNameExist();
        }

        this.fileNameErrorLabel.setVisible(!this.isNameValid);
        this.setExecuteStatus();
    }


    public void onPasswordFieldEdited() {}


    public void setEncryptStatus() {}


    public void closeWindow() {
        stage.close();
    }

//    void initData(Stage stage, String currentPath, Map<String, File> selectedChildrenPaths); //The method to pass in data from outside of the class
//    void onWindowDragged(); //Method triggered whenever the window is dragged
//    void freezeWindowProperties(); //Method called to freeze the content of the page
//    void onExecuteComplete(); //Method triggered upon the completion of a task
//    void execute(); //Method called to perform a task
//    void setExecuteStatus(); //Method used to set the execute status
//    void checkNameExist(); //Method used to check if a file name is exist
//    void onPathFieldEdited(); //Method triggered whenever the path field is edited
//    void onPathFieldComplete(); //Method triggered upon finish editing the path field
//    void onChoosePath(); //Method used to choose path
//    void onNameFieldEdited(); //Method triggered whenever the name field is edited
//    void setEncryptStatus(); //Method used to set the encryption status
//    void onPasswordFieldEdited(); //Method triggered whenever the password field is edited
//    void closeWindow(); //Method called to close window


}
