package Jialat.SubPage;

import Jialat.Dialogs;
import Jialat.ProgressTask;

import java.io.File;
import java.net.URL;
import java.util.*;

import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import net.lingala.zip4j.*;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;


/** ZipSubPageController Class
 *  The class implements SubPageController Interface, it is a sub page of the main page. It is used add information or configuration of a zip before the zip execution, such as setting the destination path, destination zip file name, and password.
 */
public class ZipSubPageController implements SubPageController {


    public TextField zipPathField = new TextField(); //Zip destination path text field
    public Button zipPathChooserButton = new Button(); //Zip path chooser button
    public Label pathErrorLabel = new Label(); //Error label for path
    public TextField fileNameField = new TextField(); //Text field for zipped file destination name
    public Label fileNameErrorLabel = new Label(); //Error label for name
    public CheckBox encryptCheckBox = new CheckBox(); //Check box for enabling/disabling encryption
    public PasswordField passwordField = new PasswordField(); //Password text field
    public Label passwordError = new Label(); //Error label for password
    public ProgressBar progressBar = new ProgressBar(); //Progress bar
    public Button executeZipButton = new Button(); //Execute zip button
    public Button cancelButton = new Button(); //Cancel button for closing window

    private Stage stage; //SubPage Stage
    private Map<String, File> selectedChildrenPaths; //Map for the selected children path (selected map)
    private boolean isPathValid = true; //Indicator of valid path
    private boolean isNameValid = false; //Indicator of valid name
    private boolean isPasswordValid = false; //Indicator for valid password
    private String currentPath; //The current path

    private double xOffSet = 0; //Mouse X offset
    private double yOffset = 0; //Mouse Y offset

    private Dialogs dialogs = new Dialogs(); //Instantiate a new Dialog


    /** initialize Method
     *  The function implements the Initialize Interface method. Used in initializing a controller from its root element.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {}


    /** initData Method
     * @param stage: The stage of the sub page
     * @param currentPath: The current path (last located in the explorer class)
     * @param selectedChildrenPaths:The map of selected files
     *  The initData method is used to pass in data from outside of the object. There are 3 necessary data, the stage of the particular sub page, the current path (last path where the user is in), and selectedChildrenPaths (the selected files map).
     */
    @Override
    public void initData(Stage stage, String currentPath, Map<String, File> selectedChildrenPaths) {
        this.stage = stage; //Assigns the stage
        this.currentPath = currentPath; //Assigns the current path
        this.selectedChildrenPaths = selectedChildrenPaths; //Assigns the selected map

        this.zipPathField.setText(this.currentPath); //Sets the zipPathField to the current path
    }


    /** onWindowDragged Method
     *  This method is triggered when the window is being dragged, it will follow the movement of the mouse.
     */
    @Override
    public void onWindowDragged() {
        //Getting position when the window is pressed
        this.stage.getScene().setOnMousePressed((event -> {
            this.xOffSet = event.getSceneX();
            this.yOffset = event.getSceneY();
        }));

        //Change the window's position based on the movement of mouse
        this.stage.getScene().setOnMouseDragged((event -> {
            this.stage.setX(event.getScreenX() - this.xOffSet);
            this.stage.setY(event.getScreenY() - this.yOffset);
        }));
    }


    /** freezeWindowProperties Method
     *  This method is called to freeze (disable) all of the nodes/components in the window such as zipPathField, fileNameField, encryptCheckBox, passwordField, executeZipButton, cancelButton, and zipPathChooserButton.
     */
    @Override
    public void freezeWindowProperties() {
        this.zipPathField.setDisable(true); //Disable zipPathField
        this.fileNameField.setDisable(true); //Disable fileNameField
        this.encryptCheckBox.setDisable(true); //Disable encryptCheckBox
        this.passwordField.setDisable(true); //Disable passwordField
        this.executeZipButton.setDisable(true); //Disable executeZipButton
        this.cancelButton.setDisable(true); //Disable cancelButton
        this.zipPathChooserButton.setDisable(true); //Disable zipPathChooserButton
    }


    /** onExecuteComplete Method
     *  The method triggered upon the completion of zip/unzip execution. It will set the name of cancelButton from "Cancel" to "Done" and enable it.
     */
    @Override
    public void onExecuteComplete() {
        this.cancelButton.setDisable(false); //Enables the cancel button
        this.cancelButton.setText("Done"); //Sets the button text to "Done"
    }


    /** execute Method
     *  The execute method implements the execute method from MainPageController abstract class, triggered when the execute button is pressed and used to start zip process. Before the execution, all of the nodes/components of the window will be frozen by calling freezeWindowProperties(). Then, all of the information (destination path, name, password, and files) will be passed into the ZipFile Object (which uses zip4j zipper) once all the information meets the criteria (valid). There are two types of zip execution, the one with password and the one without password, this can be set by check/uncheck the encryptCheckBox. During the execution, there are 2 threads run in the background, the one responsible for the zip itself and for updating the progress bar.
     *  Note that in the current version of the program, if multiple files are selected and a folder is also included in that files, the content of the folder will not be zip but only the folder. To zip a folder, the user need to select only that folder, then it will be able to zip all the contents of the folder.
     *  Also note that if the files are folder/directory, the zip file WILL NOT be encrypted.
     */
    @Override
    public void execute() {
        //TRY-CATCH
        try {
            ZipFile zipFile = new ZipFile(this.zipPathField.getText() + "/" + this.fileNameField.getText() + ".zip"); //Instantiate a new ZipFile and passes the destination path along with the name of file with .zip type
            ZipParameters zipParameters = new ZipParameters(); //Instantiate a new zipParameters
            ProgressTask progressTask = new ProgressTask(this.progressBar, zipFile.getProgressMonitor()); //Instantiate a new ProgressTask and passes the progress bar as well as the progress monitor

            progressTask.setOnSucceeded(event -> this.onExecuteComplete()); //Calls onExecuteComplete() upon the completion of the zip task

            zipFile.setRunInThread(true); //Sets zip execution to run in background (create a thread for zip execution)
            if (this.isPasswordValid) {
                //If the password needed and is valid
                zipParameters.setEncryptFiles(true); //Sets the zip to encrypt the data
                zipParameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD); //Sets the encryption method
                zipFile.setPassword(this.passwordField.getText().toCharArray()); //Passes the password
            }
            System.out.println(zipFile.isEncrypted());
            this.freezeWindowProperties(); //Freezes the window's nodes/components
            ArrayList<File> tempSelectedChildrenPath = new ArrayList<File>(this.selectedChildrenPaths.values()); //Gets the value (File Object) of selectedChildrenPaths map and converts it to an ArrayList
            if (tempSelectedChildrenPath.size() == 1 && tempSelectedChildrenPath.get(0).isDirectory()) {
                //If the selected map only contains one directory file
                zipFile.addFolder(tempSelectedChildrenPath.get(0), zipParameters); //Passes the folder (which includes all the content of that specific folder) along zipParameters into the ZipFile
            } else {
                //If the selected map contains multiple files
                zipFile.addFiles(tempSelectedChildrenPath, zipParameters); //Passes all the files and folders but not the content of the folders
            }
            new Thread(progressTask).start(); //Start the progress tracking

        } catch (ZipException ex) {
            //Catches zip exceptions
            this.dialogs.alert("Unexpected Error!", ex.getLocalizedMessage()); //Shows alert
            this.onExecuteComplete(); //calls onExecuteComplete()
        }
    }


    /** setExecuteStatus Method
     *  Sets the zip execution status. This method will be called in any of the method in this class. It is used to update the enable or disable status of execute zip button. Once all of the information needed are valid, the execute zip button will be enabled otherwise remain disabled.
     */
    @Override
    public void setExecuteStatus() {
        if (this.isPathValid && this.isNameValid) {
            //If the path and name are valid
            if ((this.encryptCheckBox.isSelected() && this.isPasswordValid) || !this.encryptCheckBox.isSelected()) {
                //If the encrypt check bos is selected and the password is valid or the encrypt check bos is not selected
                this.executeZipButton.setDisable(false); //Enables the execute zip button
                return;
            }
        }
        this.executeZipButton.setDisable(true); //Disables the execute zip button
    }


    /** checkNameExist Method
     *  This method is used to check the existence of file name inputted inside the name field. If there is a file exist with that name and is in the same path, the isNameValid will be set to false, sets the error label to "File name already exist", and shows it. Otherwise, it will set the isNameValid to true and hide the error label. Finally it will call setExecuteStatus() to update the execute status.
     */
    @Override
    public void checkNameExist() {
        File currentDir = new File(this.currentPath); //Instantiate a new File with current path

        if (Objects.requireNonNull(currentDir.listFiles((file, name) -> name.equals(fileNameField.getText() + ".zip"))).length != 0) {
            //If the name used is already being used in other file of the same path
            this.isNameValid = false; //Set isNameValid to false
            this.fileNameErrorLabel.setText("File name already exist"); //Shows name error label
        } else {
            //If the name hasn't been used in the path
            this.isNameValid = true; //Set isName valid to true
        }
        this.fileNameErrorLabel.setVisible(!this.isNameValid); //Hides the name error label
        this.setExecuteStatus(); //Calls setExecuteStatus()
    }


    /** onPathFieldEdited Method
     * Called when any change is done on zipPathField.
     * As soon as a key is released, an event will be triggered, which calls upon this function to check if the content (path text) in zipPathField is a directory. The function will set isPathValid to false and pathErrorLabel to visible in case the directory path is not valid, else it will set isPathValid to true, update the currentPath and call checkFileNameExist to ensure the file name in fileNameField is not equal to any files in the directory path. This function is responsible to enable the executeZipButton by calling setExecuteZipStatus function to refresh the button as long as the final path in zipPathField is valid.
     */
    @Override
    public void onPathFieldEdited() {
        //Checks if the zip path destination path is in a directory
        if (!new File(this.zipPathField.getText()).isDirectory()) {
            //Destination path is not a directory
            this.isPathValid = false; //Sets the path to invalid
        } else {
            //Destination path is a directory
            this.isPathValid = true; //Sets the path to valid
            this.currentPath = this.zipPathField.getText(); //Sets the current path to the text in the path text field
            this.checkNameExist(); //Calls checkNameExist()
        }

        this.pathErrorLabel.setVisible(!this.isPathValid); //Shows error if path is invalid and hide it if the path is valid
        this.setExecuteStatus(); //Calls setExecuteStatus()
    }


    /** onPathFieldComplete Method
     * The function is called upon the completion of zipPathField editing which is by pressing enter key. It sets the zipPathField to the currentPath (temporarily storing last valid path) if the input path is not valid. Then it will call setExecuteZipStatus to updates the availability of executeZipButton.
     */
    @Override
    public void onPathFieldComplete() {
        if (!this.isPathValid) {
            //If the path is invalid
            this.zipPathField.setText(this.currentPath); //Sets the zipPathField text to current path
        }
        this.setExecuteStatus(); //Calls setExecuteStatus()
    }


    /** onChooserPath Method
     * The function is triggered when zipPathChooserButton is pressed. It shows a DirectoryChooser dialog, allowing user to choose the directory location for storing zipped file. The function updates zipPathField, checks the validity of the selected directory path by calling onZipPathFieldEdited, and updates the availability of executeZipButton if a directory is chosen from the dialog. On the other hand, the function does nothing when the operation is canceled.
     */
    @Override
    public void onChoosePath() {
        DirectoryChooser dc = new DirectoryChooser(); //Instantiate a new Directory Chooser
        File selectedDir = dc.showDialog(this.stage); //Gets the selected directory

        if (selectedDir != null) {
            //If the selected directory is not null
            this.zipPathField.setText(selectedDir.getPath()); //Sets the zipPathField text to the path of selected directory
            this.currentPath = this.zipPathField.getText(); //Sets the current path to the path of selected directory
            this.checkNameExist(); //Calls checkNameExist()
            this.setExecuteStatus(); //Calls setExecuteStatus()
        }
    }


    /** onNameFieldEdited Method
     *  This particular method is triggered when the name field is edited. Every time a key is release it will check for the validation of the name. If the name field is empty, it will set isNameValid to false and sets the error label to "Name field must be filled", else it will calls checkNameExist() to perform further name validation check which will be implemented in the children class. Next, it will show the name error depend on the isNameValid and then calls the setExecuteStatus().
     */
    @Override
    public void onNameFieldEdited() {
        if (this.fileNameField.getText().isEmpty()) {
            //If the fileNameField is empty
            this.isNameValid = false; //Set the name to invalid
            this.fileNameErrorLabel.setText("Name field must be filled"); //Sets the text of error label
        } else {
            //If the fileNameField is not empty
            this.checkNameExist(); //Calls checkNameExist()
        }

        this.fileNameErrorLabel.setVisible(!this.isNameValid); //Sets the visibility of error label
        this.setExecuteStatus(); //Calls setExecuteStatus()
    }


    /** setEncryptStatus Method
     *  The method responsible set the encryption status. It is triggered when the encrypt check box is modified. If the check box is selected, the password field will be enabled and disabled when the check box is unselected. Once modified, the setExecuteStatus() will be called to update the execution status because the availability of password has been modified.
     */
    @Override
    public void setEncryptStatus() {
        if (this.encryptCheckBox.isSelected()) {
            //If the encryptCheckBos is selected
            this.passwordField.setDisable(false); //Enables the password field
        } else {
            //If the encryptCheckBos is unselected
            this.passwordField.clear();
            this.passwordField.setDisable(true); //Disables the password field
        }
        this.setExecuteStatus(); //Calls setExecuteStatus()
    }


    /** onPasswordFieldEdited Method
     *  Triggered whenever a key is release when accessing the password text field. It will sets the validation of password to true if and only if the length of password is not less than 8 characters. Then it will set the visibility of password error label depends on isPasswordValid and finally calls setExecuteStatus to update the status of execution.
     */
    @Override
    public void onPasswordFieldEdited() {
        if (this.passwordField.getText().length() < 8) {
            //If password is less than 8 characters
            this.isPasswordValid = false; //Sets isPasswordValid to false
        }  else {
            //If password is not less than 8 characters
            this.isPasswordValid = true; //Sets isPasswordValid to true
        }

        this.passwordError.setVisible(!this.isPasswordValid); //Sets the visibility of error label
        this.setExecuteStatus(); //Calls setExecuteStatus()
    }


    /** closeWindow Method
     *  This method is called to close the window.
     */
    @Override
    public void closeWindow() {
        this.stage.close(); //Closer window
    }
}