/*
 * @File Starter.java
 * @Authors : David González León
 * @Date 11 mai 2021
 */
package ch.heigvd.robotpi.servertest;

import java.util.Scanner;

public class Starter {
   public static void main(String[] args) {
      Server server = new Server("good", false);
      Thread thread = new Thread(server);
      thread.start();
      System.out.println("Enter \"stop\" to stop the server from this terminal");
      Scanner scanner = new Scanner(System.in);
      while (true) {
         String response = scanner.nextLine();
         if (response.equals("stop")) {
            System.out.println("Stopping execution of the server");
            server.stopExecution();
            try {
               thread.join();
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
            break;
         }
      }
      System.out.println("Server stopped, goodbye!");
   }
}
