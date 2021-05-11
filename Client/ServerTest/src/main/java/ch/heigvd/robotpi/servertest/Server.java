/*
 * @File Server.java
 * @Authors : David González León
 * @Date 11 mai 2021
 */
package ch.heigvd.robotpi.servertest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
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
   private PictureServer pictureServer;
   private final boolean testRun;
   private boolean stopRequested = false;

   /**
    * Constructor
    *
    * @param port the port to listen on
    * @param testRun
    */
   public Server(int port, String serverType, boolean testRun) {
      this.port = port;
      this.serverType = serverType;
      this.testRun = testRun;
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
      pictureServer = new PictureServer();
      Thread pictureThread = new Thread(pictureServer);
      pictureThread.start();
   }

   private void stop() throws IOException {
      LOG.log(Level.INFO, "Stop {0} server ...", serverType);
      clientSocket.close();
      in.close();
      out.close();
      serverSocket.close();
      pictureServer.stop();
   }

   public void stopExecution(){
      stopRequested = true;
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
                     return;
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
         if (!testRun){
            if (stopRequested){
               this.stop();
               return;
            }
         }
      }
   }

   /**
    * A server that handles the picture side of the robot. It also has the same behaviour as the main class Server in
    * case the attribute serverType equals "bad"
    */
   class PictureServer implements Runnable {
      final Logger LOG = Logger.getLogger(Server.class.getName());
      private ServerSocket serverSocket;
      private Socket clientSocket = null;
      private final int PORT = 2026;
      private BufferedReader in = null;
      private PrintWriter out = null;
      private BufferedOutputStream imageOut = null;
      private boolean running = true;

      private void start() throws IOException {
         LOG.log(Level.INFO, "Start {0} server ...", serverType);
         serverSocket = new ServerSocket(port);
      }

      public void stop() throws IOException {
         LOG.log(Level.INFO, "Stop {0} server ...", serverType);
         clientSocket.close();
         in.close();
         out.close();
         serverSocket.close();
         running = false;
      }

      @Override
      public void run() {
         try {
            this.start();
            this.listen();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }

      /**
       * Sends a picture to the connecting client. The picture is always the same
       * @throws IOException
       */
      private void listen() throws IOException {
         while (true){
            LOG.log(Level.INFO, "Waiting (blocking) for a new client on port {0}", port);
            clientSocket = serverSocket.accept();
            if (!running){
               break;
            }
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(),true);
            imageOut = new BufferedOutputStream(clientSocket.getOutputStream());
            String message  = in.readLine();
            LOG.log(Level.INFO, "Received first message from client {0}",message);
            if (!message.equals("PICTURE")){
               out.println("CMD_ERR");
               in.close();
               out.close();
               imageOut.close();
               clientSocket.close();
               continue;
            }
            if (serverType.equals("good")) {
               out.println("PICTURE_OK");
               String response;
               do {
                  LOG.log(Level.INFO,"Sending a picture...");
                  BufferedImage bi = ImageIO.read(PictureServer.class.getResource("logo.png"));
                  ImageIO.write(bi, "png", imageOut);
                  response = in.readLine();
               } while (!response.equals("RECEIVED_OK"));
            } else {
               out.println("PICTURE_KO");
            }
            in.close();
            out.close();
            imageOut.close();
            clientSocket.close();
         }
      }
   }
}
