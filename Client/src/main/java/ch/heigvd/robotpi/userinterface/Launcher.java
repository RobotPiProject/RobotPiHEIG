/*
 * @File Launcher.java
 * @Authors : David González León
 * @Date 17 mars 2021
 */
package ch.heigvd.robotpi.userinterface;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {
   private UIController controller;

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
