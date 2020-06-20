package Page.UnzipSubPage;

import Page.MenuController;
import Page.ProgressTask;

import java.io.File;
import java.util.*;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;


/** UnzipMenuController Class
 *  The class extends MenuController Abstract Class, it is a sub menu / sub page of the main page. It is used add information or configuration of an unzip before the unzip execution, such as setting the destination path and destination zip file name. This class implements several methods from its parent such as execute, setExecuteStatus, and checkNameExist.
 */
public class UnzipMenuController extends MenuController {


    /** execute Method
     *  The execute method implements the execute method from MainController abstract class, triggered when the execute button is pressed and used to start unzip process. Before the execution, all of the nodes/components of the window will be frozen by calling freezeWindowProperties(). Then, all of the information (destination path, folder name, and file) will be passed into the ZipFile Object (which uses zip4j zipper) once all the information meets the criteria (valid). When a zip file is password protected, a new dialog window wil pop up and ask password from user. If the password is incorrect, the destination folder will be empty. During the execution, there are 2 threads run in the background, the one responsible for the unzip itself and for updating the progress bar.
     *  Note that in the current version of the program, only one zip file can be unzipped at a time.
     */
    @Override
    public void execute() {
        //TRY-CATCH
        try {
            File file = new File(new ArrayList<File>(this.selectedChildrenPaths.values()).get(0).getPath()); //Instantiate a new file from the zip file in the selectedChildrenPaths
            ZipFile zipFile = new ZipFile(file); //Instantiate a new ZipFile and passes the zip file to be unzipped
            ProgressTask progressTask = new ProgressTask(this.progressBar, zipFile.getProgressMonitor()); //Instantiate a new ProgressTask and passes the progress bar as well as the progress monitor

            progressTask.setOnSucceeded(event -> this.onExecuteComplete()); //Calls onExecuteComplete() upon the completion of the unzip task

            zipFile.setRunInThread(true); //Sets unzip execution to run in background (create a thread for unzip execution)
            if (zipFile.isEncrypted()) {
                //If the zip file is encrypted
                String result = this.dialogs.password(); //Shows a dialog window and asks for password (calls the password method from Dialog class)

                if (result == null) {
                    //If nothing is inputted inside the password dialog (no returns)
                    return;
                }
                zipFile.setPassword(result.toCharArray()); //Inputs the password to the zip file
            }

            if (file.exists()) {
                //If the file is still exist
                this.freezeWindowProperties(); //Freezes the window's nodes/components
                zipFile.extractAll(this.zipPathField.getText() + "/" + this.fileNameField.getText()); //Unzip the zip file into the designated path
                new Thread(progressTask).start(); //Start the progress tracking
            } else {
                //If the file is accidentally cease to exist
                this.dialogs.alert("File Not Found!", "Please check the file again!"); //Shows alert
            }
        } catch (ZipException ex) {
            //Catches zip exception
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
            this.executeZipButton.setDisable(false); //Enables the execute zip button
        } else {
            //If either path and name are invalid
            this.executeZipButton.setDisable(true); //Disables the execute zip button
        }
    }


    /** checkNameExist Method
     *  This method is used to check the existence of file name inputted inside the name field. If there is a file exist with that name and is in the same path, the isNameValid will be set to false, sets the error label to "File name already exist", and shows it. Otherwise, it will set the isNameValid to true and hide the error label. Finally it will call setExecuteStatus() to update the execute status.
     */
    @Override
    public void checkNameExist() {
        File currentDir = new File(this.currentPath); //Instantiate a new File with current path

        //Filters the files
        ArrayList<File> tempList = new ArrayList<File>(Arrays.asList(Objects.requireNonNull(currentDir.listFiles())));
        tempList.removeIf((f) -> !(f.isDirectory() && f.getName().equals(this.fileNameField.getText())));

        if (tempList.size() != 0) {
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
}