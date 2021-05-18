/*
 * @File IpAdress.java
 * @Author : David González León
 * @Date 16 mai 2021
 */
package ch.heigvd.robotpi.app.userinterface.container;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Set;

/**
 * A storage class for the information received during the discovery process
 */
public class IpAdress {
   @Getter private final String ipAdress;

   /**
    * Instantiates a new Ip adress.
    *
    * @param ipAdress the ip adress
    */
   public IpAdress(String ipAdress) {
      this.ipAdress = ipAdress;
   }

   /**
    * Transforms the received Set of ipAdresses to a list of IpAdress instances
    *
    * @param items the items to transform
    *
    * @return the resulting list
    */
   public static ObservableList<IpAdress> getItems(Set<String> items) {
      ObservableList<IpAdress> list = FXCollections.observableList(new ArrayList<>());
      for (String string : items) {
         list.add(new IpAdress(string));
      }
      return list;
   }
}
