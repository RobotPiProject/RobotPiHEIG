/*
 * @File UIController.java
 * @Authors : David González León
 * @Date 24 mars 2021
 */
package ch.heigvd.robotpi.userinterface;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class UIController {
   private Scene scene;
   
   @FXML private RadioButton RBConnectedStatus;
   @FXML private TextField TFConnectionAddress;

   public void setScene(Scene scene) {
      this.scene = scene;

   }

   public void load(Stage primaryStage) {
      primaryStage.setScene(scene);
      primaryStage.showingProperty().addListener(((observableValue, oldValue, showing) -> {
         if (showing){
            primaryStage.setMinHeight(primaryStage.getHeight());
            primaryStage.setMinWidth(primaryStage.getWidth());
         }
      }));
      primaryStage.setTitle("Robot PI HEIG");
   }

   @FXML
   void connectButtonPressed(ActionEvent event) {

   }

}
