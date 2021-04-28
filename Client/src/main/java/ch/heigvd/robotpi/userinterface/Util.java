/*
 * @File Util.java
 * @Authors : David González León
 * @Date 2 avr. 2021
 */
package ch.heigvd.robotpi.userinterface;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public final class Util {
   /**
    * Creates an alert with the specified alertType, title, header and content. The new window will just wait until it
    * is closed.
    *
    * @param alertType the type of alert
    * @param title     the title
    * @param header    the header
    * @param content   the content
    */
   public static void createAlertFrame(Alert.AlertType alertType, String title, String header, String content) {
      Alert alert = new Alert(alertType);
      alert.setTitle(title);
      alert.setHeaderText(header);
      alert.setContentText(content);
      alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      alert.showAndWait();
   }
}
