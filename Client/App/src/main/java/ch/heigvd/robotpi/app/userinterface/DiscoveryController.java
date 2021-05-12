/*
 * @File DiscoveryController.java
 * @Authors : David González León
 * @Date 12 mai 2021
 */
package ch.heigvd.robotpi.app.userinterface;

import ch.heigvd.robotpi.app.communication.Client;
import ch.heigvd.robotpi.app.userinterface.settings.SettingsParams;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.io.IOException;
import java.util.Properties;

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

   }

   @FXML
   void buttonDiscoveryPressed(ActionEvent event) {

   }
}
