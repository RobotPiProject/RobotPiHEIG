/*
 * @File IpAdress.java
 * @Authors : David González León
 * @Date 16 mai 2021
 */
package ch.heigvd.robotpi.app.userinterface.container;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Set;

public class IpAdress {
   @Getter private final String ipAdress;

   public IpAdress(String ipAdress) {
      this.ipAdress = ipAdress;
   }

   public static ObservableList<IpAdress> getItems(Set<String> items) {
      ObservableList<IpAdress> list = FXCollections.observableList(new ArrayList<>());
      for (String string : items) {
         list.add(new IpAdress(string));
      }
      return list;
   }
}
