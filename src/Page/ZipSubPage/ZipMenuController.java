package Page.ZipSubPage;

import Page.Dialogs;
import Page.MenuController;
import Page.ProgressTask;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.*;
import net.lingala.zip4j.*;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;

public class ZipMenuController implements MenuController {

    @FXML
    public TextField zipPathField;
    public Button zipPathChooserButton;
    public Label pathErrorLabel;
    public TextField fileNameField;
    public Label fileNameErrorLabel;
    public CheckBox encryptCheckBox;
    public PasswordField passwordField;
    public Label passwordError;
    public ProgressBar progressBar;
    public Button executeZipButton;
    public Button cancelButton;

    private Stage stage;
    private Map<String, File> selectedChildrenPaths;
    private boolean isPathValid = true;
    private boolean isNameValid = false;
    private boolean isPasswordValid = false;
    private String currentPath;

    private double xOffSet = 0;
    private double yOffset = 0;

    Dialogs dialogs = new Dialogs();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public void initData(Stage stage, String currentPath, Map<String, File> selectedChildrenPaths) {
        this.stage = stage;
        this.currentPath = currentPath;
        this.selectedChildrenPaths = selectedChildrenPaths;

        this.zipPathField.setText(this.currentPath);
    }

    @Override
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

    @Override
    public void freezeWindowProperties() {
        this.zipPathField.setDisable(true);
        this.fileNameField.setDisable(true);
        this.encryptCheckBox.setDisable(true);
        this.passwordField.setDisable(true);
        this.executeZipButton.setDisable(true);
        this.cancelButton.setDisable(true);
        this.zipPathChooserButton.setDisable(true);
    }

    @Override
    public void onExecuteComplete() {
        this.cancelButton.setDisable(false);
        this.cancelButton.setText("Done");
    }

    @Override
    public void execute() {
        try {
            ZipFile zipFile = new ZipFile(this.zipPathField.getText() + "/" + this.fileNameField.getText() + ".zip");
            ZipParameters zipParameters = new ZipParameters();
            ProgressTask progressTask = new ProgressTask(this.progressBar, zipFile.getProgressMonitor());

            progressTask.setOnSucceeded(event -> this.onExecuteComplete());

            zipFile.setRunInThread(true);
            if (this.isPasswordValid) {
                zipParameters.setEncryptFiles(true);
                zipParameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);
                zipFile.setPassword(this.passwordField.getText().toCharArray());
            }

            this.freezeWindowProperties();
            ArrayList<File> tempSelectedChildrenPath = new ArrayList<File>(this.selectedChildrenPaths.values());
            if (tempSelectedChildrenPath.size() == 1 && tempSelectedChildrenPath.get(0).isDirectory()) {
                zipFile.addFolder(tempSelectedChildrenPath.get(0), zipParameters);
            } else {
                zipFile.addFiles(tempSelectedChildrenPath, zipParameters);
            }
            new Thread(progressTask).start();

        } catch (ZipException ex) {
            dialogs.alert("Unexpected Error!", ex.getLocalizedMessage());
            this.onExecuteComplete();
        }
    }

    @Override
    public void setExecuteStatus() {
        if (this.isPathValid && this.isNameValid) {
            if ((this.encryptCheckBox.isSelected() && this.isPasswordValid) || !this.encryptCheckBox.isSelected()) {
                this.executeZipButton.setDisable(false);
                return;
            }
        }
        this.executeZipButton.setDisable(true);
    }

    @Override
    public void checkNameExist() {
        File currentDir = new File(this.currentPath);

        if (Objects.requireNonNull(currentDir.listFiles((file, name) -> name.equals(fileNameField.getText() + ".zip"))).length != 0) {
            this.isNameValid = false;
            this.fileNameErrorLabel.setText("File name already exist");
        } else {
            this.isNameValid = true;
        }
        this.fileNameErrorLabel.setVisible(!this.isNameValid);
        this.setExecuteStatus();
    }

    /**
     * Called when any change is done on zipPathField.
     * As soon as a key is released, an event will be triggered, which calls upon this function to check if the content (path text) in zipPathField is a directory. The function will set isPathValid to false and pathErrorLabel to visible in case the directory path is not valid, else it will set isPathValid to true, update the currentPath and call checkFileNameExist to ensure the file name in fileNameField is not equal to any files in the directory path. This function is responsible to enable the executeZipButton by calling setExecuteZipStatus function to refresh the button as long as the final path in zipPathField is valid.
     * **/
    @Override
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
    @Override
    public void onPathFieldComplete() {
        if (!this.isPathValid) {
            this.zipPathField.setText(this.currentPath);
        }
        this.setExecuteStatus();
    }

    /**
     * The function is triggered when zipPathChooserButton is pressed. It shows a DirectoryChooser dialog, allowing user to choose the directory location for storing zip file. The function updates zipPathField, checks the validity of the selected directory path by calling onZipPathFieldEdited, and updates the availability of executeZipButton if a directory is chosen from the dialog. On the other hand, the function does nothing when the operation is canceled.
     * **/
    @Override
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

    @Override
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

    @Override
    public void setEncryptStatus() {
        if (this.encryptCheckBox.isSelected()) {
            this.passwordField.setDisable(false);
        } else {
            this.passwordField.clear();
            this.passwordField.setDisable(true);
        }
        this.setExecuteStatus();
    }

    @Override
    public void onPasswordFieldEdited() {
        if (this.passwordField.getText().length() < 8) {
            this.isPasswordValid = false;
        }  else {
            this.isPasswordValid = true;
        }

        this.passwordError.setVisible(!this.isPasswordValid);
        this.setExecuteStatus();
    }

    @Override
    public void closeWindow() {
        stage.close();
    }
}