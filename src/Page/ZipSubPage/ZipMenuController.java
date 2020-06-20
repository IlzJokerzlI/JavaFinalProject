package Page.ZipSubPage;

import Page.MenuController;
import Page.ProgressTask;

import java.io.File;
import java.util.*;

import net.lingala.zip4j.*;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;


/** ZipMenuController Class
 *  The class extends MenuController Abstract Class, it is a sub menu / sub page of the main page. It is used add information or configuration of a zip before the zip execution, such as setting the destination path, destination zip file name, and password. This class implements several methods from its parent such as execute, setExecuteStatus, checkNameExist, setEncryptStatus, and onPasswordFieldEdited.
 */
public class ZipMenuController extends MenuController {


    /** execute Method
     *  The execute method implements the execute method from MainController abstract class, triggered when the execute button is pressed and used to start zip process. Before the execution, all of the nodes/components of the window will be frozen by calling freezeWindowProperties(). Then, all of the information (destination path, name, password, and files) will be passed into the ZipFile Object (which uses zip4j zipper) once all the information meets the criteria (valid). There are two types of zip execution, the one with password and the one without password, this can be set by check/uncheck the encryptCheckBox. During the execution, there are 2 threads run in the background, the one responsible for the zip itself and for updating the progress bar.
     *  Note that in the current version of the program, if multiple files are selected and a folder is also included in that files, the content of the folder will not be zip but only the folder. To zip a folder, the user need to select only that folder, then it will be able to zip all the contents of the folder.
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
            dialogs.alert("Unexpected Error!", ex.getLocalizedMessage()); //Shows alert
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
}