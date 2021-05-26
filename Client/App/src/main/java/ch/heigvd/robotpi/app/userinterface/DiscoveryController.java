/*
 * @File DiscoveryController.java
 * @Authors : David González León
 * @Date 12 mai 2021
 */
package ch.heigvd.robotpi.app.userinterface;

import ch.heigvd.robotpi.app.communication.Client;
import ch.heigvd.robotpi.app.userinterface.container.IpAdress;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Callback;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.util.Set;

/**
 * This class allows to control the discovery ui. It is linked to the discoveryView.fxml.
 */
public class DiscoveryController {
   private Scene scene;
   private Client client;
   private TextField futurIpAddress;

   @FXML private TableView<IpAdress> TVObjectsDiscovered;
   @FXML private TableColumn<IpAdress, String> TCIpAdress;
   @FXML private TableColumn<IpAdress, Void> TCSelect;
   @FXML private Label LDiscovery;

   /**
    * Sets the scene linked to this controller and sets up all of it's components
    *
    * @param scene  the scene
    * @param client the client
    */
   public void setScene(Scene scene, Client client, TextField futurIpAddress) {
      this.scene = scene;
      this.client = client;
      this.futurIpAddress = futurIpAddress;
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
      //Setup skin
      JMetro jMetro = new JMetro(Style.LIGHT);
      jMetro.setScene(scene);
   }

   /**
    * Function executed when the dicovery button is pressed on the ui.
    *
    * @param event the event
    */
   @FXML
   public void buttonDiscoveryPressed(ActionEvent event) {
      LDiscovery.setText("Discovering, please wait...");

      try {
         Set<String> addr = client.launchServiceDiscovery();
         TCIpAdress.setCellValueFactory(new PropertyValueFactory<>("ipAdress"));
         Callback<TableColumn<IpAdress, Void>, TableCell<IpAdress, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<IpAdress, Void> call(final TableColumn<IpAdress, Void> param) {
               return new TableCell<>() {

                  private final Button btn = new Button("Select this address");

                  {
                     btn.setOnAction((ActionEvent event) -> {
                        IpAdress adress = getTableRow().getItem();
                        futurIpAddress.setText(adress.getIpAdress());
                        ((Stage)getTableView().getScene().getWindow()).close();
                     });
                  }

                  @Override
                  public void updateItem(Void item, boolean empty) {
                     super.updateItem(item, empty);
                     if (empty) {
                        setGraphic(null);
                     } else {
                        setGraphic(btn);
                     }
                  }
               };
            }
         };
         TCSelect.setCellFactory(cellFactory);

         TVObjectsDiscovered.setItems(IpAdress.getItems(addr));
         LDiscovery.setText("Found " + addr.size() + " devices active near you.");
      } catch (InterruptedException e) {
         e.printStackTrace();
      }

   }
}
