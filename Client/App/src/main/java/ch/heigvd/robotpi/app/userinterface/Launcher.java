/*
 * @File Launcher.java
 * @Authors : David González León
 * @Date 17 mars 2021
 */
package ch.heigvd.robotpi.app.userinterface;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The un-official launching class of the client application. This class extends the Application class from the
 * javafx package, and allows the launching of the main ui of the client applications
 */
public class Launcher extends Application {
   private UIController controller;

   /**
    * The entry point of application.
    *
    * @param args the input arguments
    */
   public static void main(String[] args) {
      launch();
   }

   @Override
   public void start(Stage stage) throws Exception {
      FXMLLoader uiLoader = new FXMLLoader();
      uiLoader.setLocation(getClass().getClassLoader().getResource("mainView.fxml"));
      Scene loginScene = new Scene(uiLoader.load());
      controller = uiLoader.getController();
      controller.setScene(loginScene);

      controller.load(stage);
      stage.show();
      stage.setMaximized(true);
   }

   @Override
   public void stop() throws Exception {
      super.stop();
      controller.close();
   }
}
