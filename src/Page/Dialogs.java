package Page;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.Optional;


/** Dialog Class
 *  The Dialog class contains all the dialogs used in the pages such as the Password Dialog (asks for password) and Alert Dialog (shows alert)
 * **/
public class Dialogs {

    /** password Method
     *  The password method is called whenever password is needed from the user. It will shows a pop up which asks password from the user and returns upon completed.
     * **/
    public String password() {
        Dialog<String> dialog = new Dialog<String>(); //Initiation of Dialog object
        PasswordField passwordField = new PasswordField(); //Password field input
        VBox content = new VBox(); //Root content
        Optional<String> result; //Contains password

        //Sets content's properties of Dialog
        content.setAlignment(Pos.CENTER_LEFT);
        content.setSpacing(10);
        content.getChildren().addAll(new Label("This zip is encrypted and require password:"), passwordField);

        //Sets Dialog properties
        dialog.setTitle("Password"); //Title
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL); //Buttons
        dialog.getDialogPane().setContent(content); //Sets content of Dialog

        //Listen to events
        dialog.setResultConverter(dialogButton -> {
            if(dialogButton == ButtonType.OK) {
                //Returns password if OK button is pressed
                return passwordField.getText();
            }
            //Returns null if OK button is not pressed
            return null;
        });

        //Show dialog and wait for user's respond
        result = dialog.showAndWait();

        //Checks user's response
        if (!result.isPresent()) {
            //Returns null
            return null;
        }
        //Returns result (password)
        return result.get();
    }


    /** alert Method
     *  The alert method is called whenever a user alert is necessary in the pages. I will simply show the alert message and wait for the confirmation from the user.
     * **/
    public void alert(String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.WARNING); //Instantiate Alert Object
        alert.setHeaderText(headerText); //Sets the header
        alert.setContentText(contentText); //Sets the content
        alert.showAndWait(); //Shows the alert dialog and wait confirmation from the user
    }
}
