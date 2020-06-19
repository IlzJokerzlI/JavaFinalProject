package Page.UnzipSubPage;

import Page.MenuController;
import Page.Dialogs;

import Page.ProgressTask;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.net.URL;
import java.util.*;

public class UnzipMenuController implements MenuController {

    @FXML
    public TextField zipPathField;
    public Button zipPathChooserButton;
    public Label pathErrorLabel;
    public TextField fileNameField;
    public Label fileNameErrorLabel;
    public ProgressBar progressBar;
    public Button executeZipButton;
    public Button cancelButton;

    private Stage stage;
    private Map<String, File> selectedChildrenPaths;
    private boolean isPathValid = true;
    private boolean isNameValid = false;
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
            File file = new File(new ArrayList<File>(this.selectedChildrenPaths.values()).get(0).getPath());
            ZipFile zipFile = new ZipFile(file);
            ProgressTask progressTask = new ProgressTask(this.progressBar, zipFile.getProgressMonitor());

            progressTask.setOnSucceeded(event -> this.onExecuteComplete());

            zipFile.setRunInThread(true);
            if (zipFile.isEncrypted()) {
                String result = this.dialogs.password();

                if (result == null) {
                    return;
                }
                zipFile.setPassword(result.toCharArray());
            }

            if (file.exists()) {
                this.freezeWindowProperties();
                zipFile.extractAll(this.zipPathField.getText() + "/" + this.fileNameField.getText());
                new Thread(progressTask).start();
            } else {
                this.dialogs.alert("File Not Found!", "Please check the file again!");
            }
        } catch (ZipException ex) {
            this.dialogs.alert("Unexpected Error!", ex.getLocalizedMessage());
        }
    }

    @Override
    public void setExecuteStatus() {
        if (this.isPathValid && this.isNameValid) {
            this.executeZipButton.setDisable(false);
        } else {
            this.executeZipButton.setDisable(true);
        }
    }

    @Override
    public void onPathFieldComplete() {
        if (!this.isPathValid) {
            this.zipPathField.setText(this.currentPath);
        }
        this.setExecuteStatus();
    }

    @Override
    public void checkNameExist() {
        File currentDir = new File(this.currentPath);

        ArrayList<File> tempList = new ArrayList<File>(Arrays.asList(Objects.requireNonNull(currentDir.listFiles())));
        tempList.removeIf((f) -> !(f.isDirectory() && f.getName().equals(this.fileNameField.getText())));

        if (tempList.size() != 0) {
            this.isNameValid = false;
            this.fileNameErrorLabel.setText("File name already exist");
        } else {
            this.isNameValid = true;
        }
        this.fileNameErrorLabel.setVisible(!this.isNameValid);
        this.setExecuteStatus();
    }

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

    }

    @Override
    public void onPasswordFieldEdited() {

    }

    @Override
    public void closeWindow() {
        stage.close();
    }
}