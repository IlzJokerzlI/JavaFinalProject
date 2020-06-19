package Page.MainPage;

import Page.MenuController;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.Map;


/** MainController Class
 *  The main controller class is the main page of the Jialat Zipper Application. It is mainly used to find any files files to zip or zip files to unzip.
 * **/
public class MainController implements Initializable{

    public TextField pathField; // Text field to search for path
    public Button clearPathButton; //Button to clear path text field
    public Button searchPathButton; //Button to search path text field

    public Button addFileButton; //Button to add file to selected list
    public Button removeFileButton; //Button to remove file from selected list
    public Button clearFileButton; //Button to clear the selected list

    public ListView<String> explorerListView; //The list view for exploring files
    public ListView<String> selectedListView; //The list view for selected files

    public ComboBox<String> comboBox; //Combo box to select zip mode or unzip mode
    public Button executeButton; //Button to execute zip or unzip

    private final String defaultPath = "/home"; //Default path
    private String currentPath = this.defaultPath; //The current path of explorer

    private boolean isZip = true; //Determines the process to zip or unzip

    Map<String, File> tempChildrenPaths = new LinkedHashMap<String, File>(); //The files in the explorer list (current list)
    Map<String, File> selectedChildrenPaths = new LinkedHashMap<String, File>(); //The selected files (selected list)


    /** initialize Method
     *  The initialize method is implemented from Initializeable Interface, it is used to initialize a controller from its root element. This method allows to perform several task when the fxml is successfully loaded. There are 2 task to perform, setZip() and displayFiles(). The setZip() sets the content of combo box and selects default zip mode, the displayFiles() sets the initial content of explorer list view, which is the files contained in the current path (default path).
     * **/
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setZip(); //Sets zip mode
        displayFiles(); //Displays files in explorer list view
    }


    /** toDefault Method
     *  This methods sets everything to its default just like how it is when the application is started. This includes setting the path to default path, clear all selected items and re-showing the default explorer list view.
     * **/
    public void toDefault() {
        this.currentPath = defaultPath; //Sets current path to default path
        this.tempChildrenPaths.clear(); //Clear the files in explorer list
        this.selectedChildrenPaths.clear(); //Clear the files in selected list
        this.selectedListView.getItems().clear(); //Clear the selected list view
        displayFiles(); //Re-showing the explorer list view with files in the default path
    }


    /** setZip Method
     *  The setZip method sets the mode of the application which can be switches between zip and unzip mode. The zip mode lets user to zip files and the unzip mode lets the user unzip a zip file. When zip mode is selected, it will sets isZip to true and list view selection model to be able to select multiple files. When unzip mode is selected, it will sets isZip to false and only allows selection model to only be able to select one file at a time. The default mode is zip which makes the isZip's default value true.
     * **/
    public void setZip() {
        this.explorerListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE); //Sets the explorer list view to be able to select multiple files
        this.selectedListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE); //Sets the selected list view to be able to select multiple files
        if (this.comboBox.getItems().isEmpty()) {
            //If the mode combo box is empty
            this.comboBox.getItems().addAll("Zip", "Unzip"); //Adds zip and unzip option
            this.comboBox.setValue("Zip"); //Sets the value to default (zip)
        } else if (comboBox.getValue().equals("Unzip")) {
            //If the mode selected in combo box is unzip
            this.explorerListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE); //Sets the explorer list view to only be able to select one file at a time
            this.selectedListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE); //Sets the selected list view to only be able to select one file at a time
            this.isZip = false; //Sets the isZip to false (indicates that it is no longer zip mode)
            this.executeButton.setText("Unzip"); //Sets the execute button name to unzip
            toDefault(); //Calls toDefault() to reset everything to default
        } else {
            //If the mode selected in combo box is zip
            this.isZip = true; //Sets the isZip to true (indicates that is in zip mode)
            this.executeButton.setText("Zip"); //Sets the execute button name to zip
            toDefault(); //Calls toDefault() to reset everything to default
        }
    }


    /** displayFiles method
     *  This method displays all the files in the directory of current path into the explorer list as well as updates the path text field, it will be updated whenever the current path is changed. The display depends on which mode is selected, all folders will always be shown in both mods while the hidden files will remain hidden and will not be displayed in any of the modes. The zip mode displays all available type of files. On the other hand, unzip mode only displays available zip type files.
     *  The method will first clear the explorer list view and the tempChildrenListView (which contains the list of files in the previous path). Then, the files in the current path will be filtered before inserted into tempChildrenListView (name of file being the key and File Object being the value). Finally, the name of files in the tempChildrenListView will be taken and shown in the explorer list view as well as updates the path text field.
     * **/
    public void displayFiles() {
        File currentDir = new File(this.currentPath); //Instantiate a new file (directory) from the current path

        //Checks if the file exist and is a directory
        if (currentDir.exists() && currentDir.isDirectory()) {
            this.tempChildrenPaths.clear(); //Clear the list of files tempChildrenPaths
            this.explorerListView.getItems().clear(); //Clear the list of file names in explorer list view

            this.tempChildrenPaths.put("..", currentDir.getParentFile()); //Adds back option

            if (this.isZip) {
                //Zip mode

                //Filter the files in the current directory before inputted into tempChildrenPaths
                for (File childFile : Objects.requireNonNull(currentDir.listFiles(file -> file.exists() && !file.isHidden()))) {
                    this.tempChildrenPaths.put(childFile.getName(), childFile);
                }
            } else {
                //Unzip mode

                //Filter the files in the current directory before inputted into tempChildrenPaths
                for (File childFile : Objects.requireNonNull(currentDir.listFiles(file -> (file.isDirectory() || file.toString().endsWith(".zip")) && !file.isHidden()))) {
                    this.tempChildrenPaths.put(childFile.getName(), childFile);
                }
            }

            this.explorerListView.getItems().addAll(this.tempChildrenPaths.keySet()); //Adds all the file names into list view
            this.pathField.setText(currentDir.getPath()); //Updates the path text field
        }
    }


    /** clearPath Method
     *  Sets the current path to default path and calls displayFiles() to update the displayed files as well as the path text field whenever the clearPathButton is pressed.
     * **/
    public void clearPath() {
        this.currentPath = this.defaultPath; //Sets the current path to default path
        displayFiles(); //Calls displayFiles method
    }


    /** searchPath Method
     *  Search for the directory of path inserted in the path text field. This method is triggered by searchPathButton. It will sets the current path into the path in the path text field and calls display files to update the displayed files as well as the path text field if the file of the searched path exist and is a directory. Otherwise, it will set the path text field to current path.
     * **/
    public void searchPath() {
        File searchedFile = new File(this.pathField.getText()); //Instantiate a new file from the path inserted into the path text field

        //Checks if the file exist and is a directory
        if (searchedFile.exists() && searchedFile.isDirectory()) {
            this.currentPath = this.pathField.getText(); //Sets the current path to the path in the text field
            displayFiles(); //Calls displayFiles method
        } else {
            this.pathField.setText(this.currentPath); //Sets the path text field to current path
        }
    }


    /** openDir Method
     *  The openDir method is triggered when the content of explore list view is being double clicked. If the the content clicked is ".." (back), the current path will be set to the path contained in the value with key ".." inside the tempChildrenPaths (parrent path of the directory of the current path). If the content clicked is a directory, the current path will be set into the path contained in the value with key (name of selected item) inside the tempChildrenPath, else, it will calls addFile(). Then, displayFiles() will be called to update the explorer list view display and updates the path text field.
     * **/
    public void openDir(MouseEvent event) {
        //Checks if it is dpuble clicked
        if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
            //TRY-CATCH
            try {
                String selectedItem = this.explorerListView.getSelectionModel().getSelectedItem(); //Initialize the selected item (name of file)

                //Setting file path
                if (selectedItem.equals("..")) {
                    //If the selected item is ".." (back)
                    this.currentPath = this.tempChildrenPaths.get("..").getPath(); //Sets the current path to the parent path
                } else {
                    if ( this.tempChildrenPaths.get(selectedItem).exists() && this.tempChildrenPaths.get(selectedItem).isDirectory()) {
                        //If the selected item is a directory
                        this.currentPath = this.tempChildrenPaths.get(selectedItem).getPath(); //Sets the current path to the path of the selected item
                    } else {
                        //If the selected item is a file
                        addFile(); //Calls addFile()
                        return;
                    }
                }
                displayFiles(); //Updates the explorer list view and path text field
            } catch (NullPointerException ex) {
                //Catch NULL POINTER EXCEPTION when the selected item is null (empty)
                ex.printStackTrace(); //Output the message into console
            }
        }
    }


    /** addFile Method
     *  The addFile method is used to add files selected from the explorer list view into the selectedChildrenPath list or the selected list view. This method is triggered either by a double click in the explorer list view or the addFileButton is pressed. Every time files are being added, the selected list view will always be cleared, the selected files will be filtered (must not empty or ".." (back)) before added into the selectedChildrenPath list. Then, the file name of added files inside selectedChildrenPath will be taken and shown in the selected list view.
     *  Note that in the zip mode, user is allowed to add multiple files into the selected list. However, it is not allowed in unzip mode as the unzip only supports single file unzip.
     * **/
    public void addFile() {
        List<String> selectedItems = this.explorerListView.getSelectionModel().getSelectedItems(); //Initialize the selected items list

        this.selectedListView.getItems().clear(); //Clear the selected list view

        //Iterates through each item of selected items for checking
        for (String fileName : selectedItems) {
            if (!fileName.isEmpty() && !this.selectedChildrenPaths.containsKey(fileName) && !fileName.equals("..")) {
                //If it is not empty nor equals to ".." (back) nor already exist in the selected list
                if (this.isZip) {
                    //Zip mode
                    this.selectedChildrenPaths.put(fileName, this.tempChildrenPaths.get(fileName)); //Add multiple files into the selectedChildrenPath (selected list)
                } else {
                    //Unzip mode
                    if (this.tempChildrenPaths.get(fileName).isFile()) {
                        //Checks if it is a file
                        this.selectedChildrenPaths.clear(); //Clear the selectedChildrenPaths (selected list)
                        this.selectedChildrenPaths.put(fileName, this.tempChildrenPaths.get(fileName)); //Add the selected file into the selected list
                    }
                }
            }
        }

        this.selectedListView.getItems().addAll(this.selectedChildrenPaths.keySet()); //Updates the selected list view
    }


    /** removeFile Method
     *  Unlike the addFile method, this method is used to remove a file from selected list view or selectedChildrenPaths and it is triggered only when the removeFileButton is pressed, this is to prevent the file being accidentally being removed by a double click. This method will remove any selected items from the selected list view and selectedChildrenPaths.
     * **/
    public void removeFile() {
        for (String item : this.selectedListView.getSelectionModel().getSelectedItems()) {
            this.selectedChildrenPaths.remove(item); //Removes the files from selectedChildrenPaths
        }

        this.selectedListView.getItems().removeAll(this.selectedListView.getSelectionModel().getSelectedItems()); //Removes the item from selected list view
    }


    /** clearFile Method
     *  The clear file method is used whenever user wants to clear all items (files) in the selected list view or the selectedChildrenPaths. This is triggered by the clearFileButton.
     *
     * **/
    public void clearFile() {
        this.selectedListView.getItems().clear(); //Clear selected list view
        this.selectedChildrenPaths.clear(); //Clear selectedChildrenPaths (selected list)
    }


    /** execute Method
     *  The execute method can only be functional when the selectedChildrenPaths is not empty. If so, the method will show a new windows in which its content depends on the selected mode. The ZipSubPage will be shown if it is in zip mode and UnzipSubPage if it is unzip mode. A method from the SubPage will be called to pass in 3 data, the stage of the SubPage, current path, and the selectedChildrenPaths (selected list). Once the sub page is closed, the main page will be refreshed.
     * **/
    public void execute() {
        //TRY-CATCH
        try {
            FXMLLoader loader = new FXMLLoader(); //Instantiate a new FXML Loader

            //Checks if the selected list is empty
            if (!this.selectedChildrenPaths.isEmpty()) {
                if (this.isZip) {
                    //Zip mode
                    loader.setLocation(getClass().getResource("../ZipSubPage/zipmenuFXML.fxml")); //Sets to zip menu controller
                } else {
                    //Unzip mode
                    loader.setLocation(getClass().getResource("../UnzipSubPage/unzipmenuFXML.fxml")); //Sets to unzip menu controller
                }

                Parent subRoot = loader.load(); //Assign loader
                Scene subScene = new Scene(subRoot); //Instantiate a new Scene
                Stage subStage = new Stage(); //Instantiate a new Stage
                MenuController controller = loader.getController(); //Assign the controller
                controller.initData(subStage, this.currentPath, this.selectedChildrenPaths); //Calls a method from the sub page to pass in some data

                subStage.setScene(subScene); //Set the scene
                subStage.initModality(Modality.APPLICATION_MODAL); //Sets the modality
                subStage.initStyle(StageStyle.UNDECORATED); //Sets the stage style

                //When the sub page is closed (hidden)
                subStage.setOnHiding((event) -> {
                    this.displayFiles(); //Refresh the explorer list view if any changes are made
                    this.selectedListView.getItems().clear(); //Clear the selected list view
                    if (!this.selectedChildrenPaths.isEmpty()) {
                        //If the selected list is not empty
                        for (String key : this.selectedChildrenPaths.keySet()) {
                            //Removes the non-existence file from the selected list
                            if (!this.selectedChildrenPaths.get(key).exists()) {
                                this.selectedChildrenPaths.remove(key);
                            }
                        }
                        this.selectedListView.getItems().addAll(this.selectedChildrenPaths.keySet()); //Updates the selected list view from the updated selected list
                    }
                });
                subStage.show(); //Shows the sub page
            }
        } catch (IOException ex) {
            //Catch IO exception and output the message into console
            ex.printStackTrace();
        }
    }
}


// Another version of displayFiles() which uses stream
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
