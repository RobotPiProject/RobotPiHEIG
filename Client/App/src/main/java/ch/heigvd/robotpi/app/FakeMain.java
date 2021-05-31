/*
 * @File FakeMain.java
 * @Authors : David González León
 * @Date 18 mars 2021
 */
package ch.heigvd.robotpi.app;

import ch.heigvd.robotpi.app.userinterface.Launcher;

/**
 * The official launching class of the client application. This class is used to circumvent a problem with java-fx
 * that prevents from generating a .jar when using the main function located in the Launcher class.
 */
public class FakeMain {
   /**
    * The entry point of application.
    *
    * @param args the input arguments
    */
   public static void main(String[] args) {
      Launcher.main(args);
   }
}
