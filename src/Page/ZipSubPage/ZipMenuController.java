package Page.ZipSubPage;

import Page.MenuController;
import Page.ProgressTask;

import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.*;

import net.lingala.zip4j.*;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;


public class ZipMenuController extends MenuController {
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


}