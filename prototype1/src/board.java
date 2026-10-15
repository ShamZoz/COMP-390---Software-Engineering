// Jason Gonatas: Prototype 1 Attempt

// This program was built using the example put on geeks for geeks as a base
// Source: https://www.geeksforgeeks.org/java/javafx-button-with-examples/

// Import javafx. Even while being underlined in red, the program will still run
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class board extends Application {

    // launch the application
    public void start(Stage s)
    {
        // SET TITLE //
        s.setTitle("'Board' in the Classroom Prototype 1");

        // CREATE SUBMIT BUTTON //
        Button assign = new Button("Create New Assignment");
            // Set button position + size
            // Source: https://docs.oracle.com/javafx/2/ui_controls/button.htm
        assign.setStyle("-fx-font: 15 arial; -fx-base: #4ecac8;");
        //assign.setTranslateX(-50);
        //assign.setTranslateY(200);
 

        // CREATE A STACK PANE //
        TilePane page = new TilePane();


        // WELCOME MESSAGE CODE //
            // Get the user's name. 
            // Source: https://stackoverflow.com/questions/19990038/how-to-get-windows-username-in-java
        String username = System.getProperty("user.name");
        Label welcome = new Label("");
        welcome.setText("Welcome to 'Board' in the Classroom, " + username + "!\n");
            // Set label size. Source: https://docs.oracle.com/javafx/2/ui_controls/label.htm
        welcome.setFont(new Font("Arial", 30));
            // Set label position
        welcome.setTranslateX(10);
        welcome.setTranslateY(10);

        // Did the button work?
        Label update = new Label("");
        update.setFont(new Font("Arial", 15));
        update.setTextFill(Color.web("#0fae17"));

        // Assignment List
        Label assignmentList = new Label("");
        assignmentList.setFont(new Font("Arial", 20));
        assignmentList.setText("");

         // CREATE TEXT FIELDS //
            // Source: https://docs.oracle.com/javafx/2/ui_controls/text-field.htm

            //Define Assignment fields
        TextField assignmentName = new TextField();
        assignmentName.setPromptText("Assignment Name: ");
        assignmentName.setPrefColumnCount(10);
        assignmentName.getText();

        TextField assignmentType = new TextField();
        assignmentType.setPromptText("Assignment Type: ");
        assignmentType.setPrefColumnCount(10);
        assignmentType.getText();

        TextField assignmentClass = new TextField();
        assignmentClass.setPromptText("Assignment Class: ");
        assignmentClass.setPrefColumnCount(10);
        assignmentClass.getText();


        // action event if the button is clicked //
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                update.setText("Assignment created!");
                // Print assignment to console
                System.out.println(assignmentName.getText() + " | " + assignmentType.getText());

                // Print to page
                assignmentList.setText(assignmentList + "\nAssignment Name: " + assignmentName.getText()
                + " | Assignment Type: " + assignmentType.getText() + 
            " | Assignment Class: " + assignmentClass.getText());

            }
        };

        // when button is pressed
        assign.setOnAction(event);

        // add elements to page
        page.getChildren().add(welcome);
        page.getChildren().add(update);


        page.getChildren().add(assignmentName);
        page.getChildren().add(assignmentType);
        page.getChildren().add(assignmentClass);

        page.getChildren().add(assign);

        page.getChildren().add(assignmentList);

        // create a scene
        Scene sc = new Scene(page, 1280, 720);

        // set the scene
        s.setScene(sc);

        s.show();
    }

    // Launch the app
    public static void main(String args[])
    {
        // launch the application
        launch(args);
    }
}