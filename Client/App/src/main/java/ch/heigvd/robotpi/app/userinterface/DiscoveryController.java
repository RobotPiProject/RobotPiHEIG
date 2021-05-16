/*
 * @File DiscoveryController.java
 * @Authors : David González León
 * @Date 12 mai 2021
 */
package ch.heigvd.robotpi.app.userinterface;

import ch.heigvd.robotpi.app.communication.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class DiscoveryController {
   private Scene scene;
   private Client client;

   @FXML private TableView<?> TVObjectsDiscovered;
   @FXML private Label LDiscovery;

   /**
    * Sets the scene linked to this controller and sets up all of it's components
    *
    * @param scene the scene
    */
   public void setScene(Scene scene, Client client) {
      this.scene = scene;
      this.client = client;
   }

   /**
    * Loads onto the stage the scene, and executes the basic setup for the ui
    *
    * @param primaryStage the primary stage
    */
   public void load(Stage primaryStage) {
      primaryStage.setScene(scene);
      primaryStage.setResizable(false);
      primaryStage.setTitle("Robot PI HEIG - Discovery");
      primaryStage.getIcons().add(new Image("image/logo.png"));
   }

   @FXML
   void buttonDiscoveryPressed(ActionEvent event) {
      LDiscovery.setText("Discovering, please wait...");
      try {
         client.launchServiceDiscovery();
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }
}
