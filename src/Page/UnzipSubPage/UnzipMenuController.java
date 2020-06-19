package Page.UnzipSubPage;

import Page.MenuController;
import Page.ProgressTask;

import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.*;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;


public class UnzipMenuController extends MenuController {

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
}