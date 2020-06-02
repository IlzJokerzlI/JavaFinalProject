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
import org.w3c.dom.ls.LSOutput;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.EventHandler;
import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        displayFiles();
        this.explorerListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        this.explorerListView.setOnMousePressed(event -> openDir(event));

    }

    public void displayFiles() {
        File file = new File(this.currentPath);
        if (file.exists()) {
            List<String> tempList = new ArrayList<String>(Arrays.stream(file.listFiles()).filter((f) -> !f.isHidden()).map((f) -> f.getName()).collect(Collectors.toList())); //Scan files
            tempList.add(0,".."); //Add back option

            this.explorerListView.getItems().clear();
            this.explorerListView.getItems().addAll(tempList);
            this.pathField.setText(file.getPath());
        }
    }

    public void clearPath() {
        this.currentPath = this.defaultPath;
        displayFiles();
        this.pathField.setText(this.currentPath);
    }

    public void searchPath() {
        File tempFile = new File(this.pathField.getText());

        if (tempFile.exists() && tempFile.isDirectory()) {
            this.currentPath = this.pathField.getText();
            displayFiles();
        } else {
            this.pathField.setText(this.currentPath);
        }
    }

    public void openDir(MouseEvent event) {
        if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
            String selectedItem = this.explorerListView.getSelectionModel().getSelectedItem();
            File tempFile;

            //Setting file path
            if (selectedItem.equals("..")) {
                tempFile = new File(this.currentPath);
                this.currentPath = tempFile.getParent();
            } else {
                tempFile = new File(currentPath + "/" + selectedItem);
                if (tempFile.exists() && tempFile.isDirectory()) {
                    this.currentPath = tempFile.getPath();
                } else {
                    System.out.println(tempFile.getName());
                }
            }
            displayFiles();
        }
    }

    public void addFile() {
        List<String> selectedItems = this.explorerListView.getSelectionModel().getSelectedItems();
        selectedItems = selectedItems.stream().filter((f) -> !selectedListView.getItems().contains(f) && !f.contains("..")).collect(Collectors.toList());
        if (!selectedItems.isEmpty()) {
            selectedListView.getItems().addAll(selectedItems);
        }
    }

    public void removeFile() {

    }

    public void clearFile() {

    }
}
