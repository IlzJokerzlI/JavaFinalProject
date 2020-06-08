package MainPage;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.Map;

import javafx.stage.Modality;
import javafx.stage.Stage;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class MainController implements Initializable{

    public TextField pathField;
    public Button clearPathButton;
    public Button searchPathButton;

    public Button addFileButton;
    public Button removeFileButton;
    public Button clearFileButton;

    public ListView<String> explorerListView;
    public ListView<String> selectedListView;

    public ComboBox<String> comboBox;
    public CheckBox encryptCheckBox;
    public PasswordField passwordField;

    public Button executeButton;
    public ProgressBar progressBar;

    private final String defaultPath = "/home";
    private String currentPath = this.defaultPath;

    private boolean isZip = true;

    Map<String, File> tempChildrenPaths = new LinkedHashMap<String, File>();
    Map<String, File> selectedChildrenPaths = new LinkedHashMap<String, File>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setZip();
        displayFiles();
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

    public void toDefault() {
        this.currentPath = defaultPath;
        this.passwordField.clear();
        this.passwordField.setDisable(true);
        this.encryptCheckBox.setSelected(false);
        this.tempChildrenPaths.clear();
        this.selectedChildrenPaths.clear();
        this.selectedListView.getItems().clear();
        displayFiles();
    }


    public void setZip() {
        this.explorerListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.selectedListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        if (this.comboBox.getItems().isEmpty()) {
            this.comboBox.getItems().addAll("Zip", "Unzip");
            this.comboBox.setValue("Zip");
        } else if (comboBox.getValue().equals("Unzip")) {
            this.explorerListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            this.selectedListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            this.isZip = false;
            this.executeButton.setText("Unzip");
            toDefault();
            this.encryptCheckBox.setDisable(true);
        } else {
            this.isZip = true;
            this.executeButton.setText("Zip");
            this.encryptCheckBox.setDisable(false);
            toDefault();
        }
    }

    public void displayFiles() {
        File currentFile = new File(this.currentPath);

        this.tempChildrenPaths.clear();
        this.explorerListView.getItems().clear();

        if (currentFile.exists()) {
            this.tempChildrenPaths.put("..", currentFile.getParentFile()); //Adds back option

            if (this.isZip) {
                for (File childFile : Objects.requireNonNull(currentFile.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.exists() && !file.isHidden();
                    }
                }))) {
                    this.tempChildrenPaths.put(childFile.getName(), childFile);
                }
            } else {
                for (File childFile : Objects.requireNonNull(currentFile.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return (file.isDirectory() || file.toString().endsWith(".zip")) && !file.isHidden();
                    }
                }))) {
                    this.tempChildrenPaths.put(childFile.getName(), childFile);
                }
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
            try {
                String selectedItem = this.explorerListView.getSelectionModel().getSelectedItem();

                //Setting file path
                if (selectedItem.equals("..")) {
                    this.currentPath = this.tempChildrenPaths.get("..").getPath();
                } else {
                    if ( this.tempChildrenPaths.get(selectedItem).exists() && this.tempChildrenPaths.get(selectedItem).isDirectory()) {
                        this.currentPath = this.tempChildrenPaths.get(selectedItem).getPath();
                    } else {
                        addFile();
                        return;
                    }
                }
                displayFiles();
            } catch (NullPointerException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public void addFile() {
        List<String> selectedItems = this.explorerListView.getSelectionModel().getSelectedItems();

        this.selectedListView.getItems().clear();

        for (String fileName : selectedItems) {
            if (!fileName.isEmpty() && !this.selectedChildrenPaths.containsKey(fileName) && !fileName.equals("")) {
                if (this.isZip) {
                    this.selectedChildrenPaths.put(fileName, this.tempChildrenPaths.get(fileName));
                } else {
                    if (this.tempChildrenPaths.get(fileName).isFile()) {
                        this.selectedChildrenPaths.clear();
                        this.selectedChildrenPaths.put(fileName, this.tempChildrenPaths.get(fileName));
                    }
                }
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

    public void execute() throws Exception {
        Parent root = (this.isZip) ? FXMLLoader.load(getClass().getResource("zipmenuFXML.fxml")) : FXMLLoader.load(getClass().getResource("unzipmenuFXML.fxml"));

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }
}
