package sample;

import javafx.scene.input.MouseEvent;
import net.lingala.zip4j.*;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import net.lingala.zip4j.exception.ZipException;
import org.w3c.dom.ls.LSOutput;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.EventHandler;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.util.Map;

public class Controller implements Initializable{

    public TextField pathField;
    public Button clearPathButton;
    public Button searchPathButton;

    public Button addFileButton;
    public Button removeFileButton;
    public Button clearFileButton;

    public ListView<String> explorerListView;
    public ListView<String> selectedListView;

    public CheckBox encryptCheckBox;
    public PasswordField passwordField;

    public Button executeButton;
    public ProgressBar progressBar;

    private final String defaultPath = "/home";
    private String currentPath = this.defaultPath;

    Map<String, File> tempChildrenPaths = new LinkedHashMap<String, File>();
    Map<String, File> selectedChildrenPaths = new LinkedHashMap<String, File>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        displayFiles();
        this.explorerListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.selectedListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

//    public void displayFiles() {
//        File file = new File(this.currentPath);
//        if (file.exists()) {
//            List<String> tempList = new ArrayList<String>(Arrays.stream(file.listFiles()).filter((f) -> !f.isHidden()).map((f) -> f.getName()).collect(Collectors.toList())); //Scan files
//            tempList.add(0,".."); //Add back option
//
//            this.explorerListView.getItems().clear();
//            this.explorerListView.getItems().addAll(tempList);
//            this.pathField.setText(file.getPath());
//        }
//    }

    public void displayFiles() {
        File currentFile = new File(this.currentPath);

        this.tempChildrenPaths.clear();
        this.explorerListView.getItems().clear();

        if (currentFile.exists()) {
            this.tempChildrenPaths.put("..", currentFile.getParentFile()); //Adds back option

            for (File childFile : Objects.requireNonNull(currentFile.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.exists() && !file.isHidden();
                }
            }))) {
                this.tempChildrenPaths.put(childFile.getName(), childFile);
            }

            this.explorerListView.getItems().addAll(this.tempChildrenPaths.keySet()); //Sets the file list in list view
            this.pathField.setText(currentFile.getPath()); //Sets the path field to the current path
        }
    }

    public void clearPath() {
        this.currentPath = this.defaultPath;
        displayFiles();
    }

    public void searchPath() {
        File searchedFile = new File(this.pathField.getText());

        if (searchedFile.exists() && searchedFile.isDirectory()) {
            this.currentPath = this.pathField.getText();
            displayFiles();
        } else {
            this.pathField.setText(this.currentPath);
        }
    }

    public void openDir(MouseEvent event) {
        if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
            String selectedItem = this.explorerListView.getSelectionModel().getSelectedItem();

            //Setting file path
            if (selectedItem.equals("..")) {
                this.currentPath = this.tempChildrenPaths.get("..").getPath();
            } else {
                if ( this.tempChildrenPaths.get(selectedItem).exists() && this.tempChildrenPaths.get(selectedItem).isDirectory()) {
                    this.currentPath = this.tempChildrenPaths.get(selectedItem).getPath();
                } else {
                    System.out.println(this.tempChildrenPaths.get(selectedItem).getName());
                    return;
                }
            }
            displayFiles();
        }
    }

    public void addFile() {
        List<String> selectedItems = this.explorerListView.getSelectionModel().getSelectedItems();

        this.selectedListView.getItems().clear();

        for (String fileName : selectedItems) {
            if (!fileName.isEmpty() && !this.selectedChildrenPaths.containsKey(fileName) && !fileName.equals("..")) {
                this.selectedChildrenPaths.put(fileName, this.tempChildrenPaths.get(fileName));
            }
        }

        this.selectedListView.getItems().addAll(this.selectedChildrenPaths.keySet());
    }

    public void removeFile() {
        this.selectedListView.getItems().removeAll(this.selectedListView.getSelectionModel().getSelectedItems());
        this.selectedChildrenPaths.remove(this.selectedListView.getSelectionModel().getSelectedItems());
    }

    public void clearFile() {
        this.selectedListView.getItems().clear();
        this.selectedChildrenPaths.clear();
    }

    public void setEncryptStatus() {
        if (this.encryptCheckBox.isSelected()) {
            this.passwordField.setDisable(false);
        } else {
            this.passwordField.clear();
            this.passwordField.setDisable(true);
        }
    }

    public void execute() {
        if (!this.selectedChildrenPaths.isEmpty()) {
            try {
                new ZipFile("/home/arvin").addFiles(new ArrayList<File>(this.tempChildrenPaths.values()));
            } catch (ZipException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
