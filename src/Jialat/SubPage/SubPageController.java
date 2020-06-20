package Jialat.SubPage;

import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.io.File;
import java.util.Map;


/** SubPageController Interface
 *  An interface which extends Initializable and implemented by its kind such as the ZipSubPage and UnzipSubPage which are used after the main window to get more information about the execution, e.g. the path where the result of zip/unzip will be located, etc.
 */
public interface SubPageController extends Initializable {
    void initData(Stage stage, String currentPath, Map<String, File> selectedChildrenPaths); //The method to pass in data from outside of the class
    void onWindowDragged(); //Method triggered whenever the window is dragged
    void freezeWindowProperties(); //Method called to freeze the content of the page
    void onExecuteComplete(); //Method triggered upon the completion of a task
    void execute(); //Method called to perform a task
    void setExecuteStatus(); //Method used to set the execute status
    void checkNameExist(); //Method used to check if a file name is exist
    void onPathFieldEdited(); //Method triggered whenever the path field is edited
    void onPathFieldComplete(); //Method triggered upon finish editing the path field
    void onChoosePath(); //Method used to choose path
    void onNameFieldEdited(); //Method triggered whenever the name field is edited
    void setEncryptStatus(); //Method used to set the encryption status
    void onPasswordFieldEdited(); //Method triggered whenever the password field is edited
    void closeWindow(); //Method called to close window
}
