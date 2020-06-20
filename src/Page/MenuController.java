package Page;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;


/** MenuController Abstract Class
 *  An abstract class which implements Initializable and extended by its kind (children) such as the ZipSubPage and UnzipSubPage. There are some methods which needs to be implemented in the children class such as execute(), setExecuteStatus(), checkNameExist(), onPasswordFieldEdited(), and setEncryptionStatus() which are highly dependent on the mode chosen (zip or unzip). This set of classes (MenuController, ZipMenuController, and UnzipMenuController) are used after the main window to get more information about the execution, e.g. the path where the result of zip/unzip will be located.
 */
public abstract class MenuController implements Initializable {

    @FXML
    public TextField zipPathField = new TextField(); //Zip/Unzip destination path text field
    public Button zipPathChooserButton = new Button(); //Zip path chooser button
    public Label pathErrorLabel = new Label(); //Error label for path
    public TextField fileNameField = new TextField(); //Text field for zipped file/unzipped folder destination name
    public Label fileNameErrorLabel = new Label(); //Error label for name
    public CheckBox encryptCheckBox = new CheckBox(); //Check box for enabling/disabling encryption
    public PasswordField passwordField = new PasswordField(); //Password text field
    public Label passwordError = new Label(); //Error label for password
    public ProgressBar progressBar = new ProgressBar(); //Progress bar
    public Button executeZipButton = new Button(); //Execute zip/unzip button
    public Button cancelButton = new Button(); //Cancel button for closing window

    protected Stage stage; //SubPage/SubMenu Stage
    protected Map<String, File> selectedChildrenPaths; //Map for the selected children path (selected map)
    protected boolean isPathValid = true; //Indicator of valid path
    protected boolean isNameValid = false; //Indicator of valid name
    protected boolean isPasswordValid = false; //Indicator for valid password
    protected String currentPath; //The current path

    protected double xOffSet = 0; //Mouse X offset
    protected double yOffset = 0; //Mouse Y offset

    protected Dialogs dialogs = new Dialogs(); //Instantiate a new Dialog


    /** initialize Method
     *  The function implements the Initialize Interface method. Used in initializing a controller from its root element.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {}


    /** initData Method
     * @param stage: The stage of the sub page / sub menu
     * @param currentPath: The current path (last located in the explorer class)
     * @param selectedChildrenPaths:The map of selected files
     *  The initData method is used to pass in data from outside of the object. There are 3 necessary data, the stage of the particular sub menu/sub page, the current path (last path where the user is in), and selectedChildrenPaths (the selected files map).
     */
    public void initData(Stage stage, String currentPath, Map<String, File> selectedChildrenPaths) {
        this.stage = stage; //Assigns the stage
        this.currentPath = currentPath; //Assigns the current path
        this.selectedChildrenPaths = selectedChildrenPaths; //Assigns the selected map

        this.zipPathField.setText(this.currentPath); //Sets the zip path field to the current path
    }


    /** onWindowDragged Method
     *  This method is triggered when the window is being dragged, it will follow the movement of the mouse.
     */
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
    public void onExecuteComplete() {
        this.cancelButton.setDisable(false); //Enables the cancel button
        this.cancelButton.setText("Done"); //Sets the button text to "Done"
    }


    /** execute Method
     *  The method called to execute zip/unzip operation. It will be implemented in the children class
     */
    public void execute() {
        System.out.println("Executed!");
    }


    /** setExecuteStatus Method
     *  The method called to set the execute status which enables the execution under certain condition. It will be implemented in the children class
     */
    public void setExecuteStatus() {
        System.out.println("Execute Status Set!");
    }


    /** checkNameExist Method
     *  The method called to check the existence of zip file/unzip folder destination name. It will be implemented in the children class
     */
    public void checkNameExist() {
        System.out.println("Name Exist Checked!");
    }


    /** onPathFieldEdited Method
     * Called when any change is done on zipPathField.
     * As soon as a key is released, an event will be triggered, which calls upon this function to check if the content (path text) in zipPathField is a directory. The function will set isPathValid to false and pathErrorLabel to visible in case the directory path is not valid, else it will set isPathValid to true, update the currentPath and call checkFileNameExist to ensure the file name in fileNameField is not equal to any files in the directory path. This function is responsible to enable the executeZipButton by calling setExecuteZipStatus function to refresh the button as long as the final path in zipPathField is valid.
     */
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
    public void onPathFieldComplete() {
        if (!this.isPathValid) {
            //If the path is invalid
            this.zipPathField.setText(this.currentPath); //Sets the zipPathField text to current path
        }
        this.setExecuteStatus(); //Calls setExecuteStatus()
    }


    /** onChooserPath Method
     * The function is triggered when zipPathChooserButton is pressed. It shows a DirectoryChooser dialog, allowing user to choose the directory location for storing zip file. The function updates zipPathField, checks the validity of the selected directory path by calling onZipPathFieldEdited, and updates the availability of executeZipButton if a directory is chosen from the dialog. On the other hand, the function does nothing when the operation is canceled.
     */
    public void onChoosePath() {
        DirectoryChooser dc = new DirectoryChooser(); //Instantiate a new Directory Chooser
        File selectedDir = dc.showDialog(this.stage); //Gets the selected directory

        if (selectedDir != null) {
            //If the selected directory is not null
            this.zipPathField.setText(selectedDir.getPath()); //Sets the zip path field text to the path of selected directory
            this.currentPath = this.zipPathField.getText(); //Sets the current path to the path of selected firectory
            this.checkNameExist(); //Calls checkNameExist()
            this.setExecuteStatus(); //Calls setExecuteStatus()
        }
    }


    /** onNameFieldEdited Method
     *  This particular method is triggered when the name field is edited. Every time a key is release it will check for the validation of the name. If the name field is empty, it will set isNameValid to false and sets the error label to "Name field must be filled", else it will calls checkNameExist() to perform further name validation check which will be implemented in the children class. Next, it will show the name error depend on the isNameValid and then calls the setExecuteStatus()
     */
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


    /** setPasswordFieldEdited Method
     *  The method triggered upon the change of password text field. It will be implemented in the children class
     */
    public void onPasswordFieldEdited() {
        System.out.println("Password Field Edited!");
    }


    /** setEncryptStatus Method
     *  The method called to enable the encryption under certain condition. It will be implemented in the children class
     */
    public void setEncryptStatus() {
        System.out.println("Encryption Status Set!");
    }


    /** closeWindow Method
     *  This method is called to close the window
     */
    public void closeWindow() {
        stage.close();
    }


// Interface
//    void initData(Stage stage, String currentPath, Map<String, File> selectedChildrenPaths); //The method to pass in data from outside of the class
//    void onWindowDragged(); //Method triggered whenever the window is dragged
//    void freezeWindowProperties(); //Method called to freeze the content of the page
//    void onExecuteComplete(); //Method triggered upon the completion of a task
//    void execute(); //Method called to perform a task
//    void setExecuteStatus(); //Method used to set the execute status
//    void checkNameExist(); //Method used to check if a file name is exist
//    void onPathFieldEdited(); //Method triggered whenever the path field is edited
//    void onPathFieldComplete(); //Method triggered upon finish editing the path field
//    void onChoosePath(); //Method used to choose path
//    void onNameFieldEdited(); //Method triggered whenever the name field is edited
//    void setEncryptStatus(); //Method used to set the encryption status
//    void onPasswordFieldEdited(); //Method triggered whenever the password field is edited
//    void closeWindow(); //Method called to close window
}
