/*
 * @File Server.java
 * @Authors : David González León
 * @Date 11 mai 2021
 */
package ch.heigvd.robotpi.servertest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements a single-threaded TCP server.
 */
public class Server implements Runnable {

   final static Logger LOG = Logger.getLogger(Server.class.getName());
   private ServerSocket serverSocket;
   private Socket clientSocket = null;
   private BufferedReader in = null;
   private PrintWriter out = null;
   private int port;
   private String serverType;

   /**
    * Constructor
    *
    * @param port the port to listen on
    */
   public Server(int port, String serverType) {
      this.port = port;
      this.serverType = serverType;
   }

   @Override
   public void run() {
      try {
         this.start();
         this.serveClients();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public void start() throws IOException {
      LOG.log(Level.INFO, "Start {0} server ...", serverType);
      serverSocket = new ServerSocket(port);
   }

   public void stop() throws IOException {
      LOG.log(Level.INFO, "Stop {0} server ...", serverType);
      clientSocket.close();
      in.close();
      out.close();
      serverSocket.close();
   }

   /**
    * This method initiates the process.
    */
   public void serveClients() throws IOException {

      while (true) {
         LOG.log(Level.INFO, "Waiting (blocking) for a new client on port {0}", port);
         clientSocket = serverSocket.accept();
         in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
         out = new PrintWriter(clientSocket.getOutputStream());

         out.println("CONN_OK");
         out.flush();
         LOG.log(Level.INFO, "send CONN_OK to client");

         String line;
         boolean shouldRun = true;

         LOG.info("Reading until client send DISCONN or closes the connection...\n");
         while ((shouldRun) && (line = in.readLine()) != null) {
            switch (line) {
               case "PING":
                  if (serverType.equals("good")) {
                     out.println("PING");
                     LOG.info("PING");
                  } else {
                     out.println("PINGG");
                     LOG.info("PINGG");
                  }
                  break;
               case "FWD":
                  if (serverType.equals("good")) {
                     out.println("FWD_OK");
                     LOG.info("FWD_OK");
                  } else {
                     out.println("FWD_KO");
                     LOG.info("FWD_KO");
                  }
                  break;
               case "BKWD":
                  if (serverType.equals("good")) {
                     out.println("BKWD_OK");
                     LOG.info("BKWD_OK");
                  } else {
                     out.println("BKWD_KO");
                     LOG.info("BKWD_KO");
                  }
                  break;
               case "ROTATE_LEFT":
                  if (serverType.equals("good")) {
                     out.println("ROTATE_LEFT_OK");
                     LOG.info("ROTATE_LEFT_OK");
                  } else {
                     out.println("ROTATE_LEFT_KO");
                     LOG.info("ROTATE_LEFT_KO");
                  }
                  break;
               case "ROTATE_RIGHT":
                  if (serverType.equals("good")) {
                     out.println("ROTATE_RIGHT_OK");
                     LOG.info("ROTATE_RIGHT_OK");
                  } else {
                     out.println("ROTATE_RIGHT_KO");
                     LOG.info("ROTATE_RIGHT_KO");
                  }
                  break;
               case "STOP":
                  if (serverType.equals("good")) {
                     out.println("STOP");
                     LOG.info("STOP");
                     // To stop the server used in ClientGoodServerTest
                     // The cli.stop() func isn't used in these test
                     this.stop();
                  } else {
                     out.println("STOPP");
                     LOG.info("STOPP");
                  }
                  break;
               case "DISCONN":
                  out.println("DISCONN_OK");
                  shouldRun = false;
                  if (serverType.equals("bad"))
                  // To stop the server used in ClientBadServerTest.
                  // cli.dissconnect() is run only once after all tests completed.
                  { this.stop(); }
            }

            out.flush();
         }
      }
   }
}
