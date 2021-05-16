/*
 * @File Server.java
 * @Authors : David González León
 * @Date 11 mai 2021
 */
package ch.heigvd.robotpi.servertest;

import javax.imageio.ImageIO;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements a single-threaded TCP server.
 */
public class Server implements Runnable {

   final static Logger LOG = Logger.getLogger(Server.class.getName());
   private final boolean testRun;
   private ServerSocket serverSocket;
   private Socket clientSocket = null;
   private BufferedReader in = null;
   private PrintWriter out = null;
   private int port;
   private String serverType;
   private PictureServer pictureServer;
   private boolean stopRequested = false;

   /**
    * Constructor
    *
    * @param port    the port to listen on
    * @param testRun
    */
   public Server(int port, String serverType, boolean testRun) {
      this.port = port;
      this.serverType = serverType;
      this.testRun = testRun;
      try {
         // Create a JmDNS instance
         JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());

         // Register a service
         ServiceInfo serviceInfo = ServiceInfo.create("_http._tcp.local.", "example", 1234, "path=index.html");
         jmdns.registerService(serviceInfo);
         LOG.log(Level.INFO, "Discovery service online");
      } catch (UnknownHostException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   @Override
   public void run() {
      try {
         this.start();
         this.serveClients();
      } catch (IOException e) {
      }
   }

   public void start() throws IOException {
      LOG.log(Level.INFO, "Start {0} server ...", serverType);
      serverSocket = new ServerSocket(port);
      pictureServer = new PictureServer();
      Thread pictureThread = new Thread(pictureServer);
      pictureThread.start();
   }

   public void stopExecution() {
      stopRequested = true;
      stop();
   }

   /**
    * This method initiates the process.
    */
   public void serveClients() throws IOException {

      while (!stopRequested) {
         LOG.log(Level.INFO, "Waiting (blocking) for a new client on port {0}", port);
         try {
            clientSocket = serverSocket.accept();
         } catch (SocketException e){
            return;
         }
         in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
         out = new PrintWriter(clientSocket.getOutputStream(), true);
         LOG.log(Level.INFO, "Received message from client on login : {0}", in.readLine());
         out.print(ProtocolCommands.conn.getMessageConfirmation() + "\n");
         out.flush();
         LOG.log(Level.INFO, "send CONN_OK to client");

         String line;
         boolean shouldRun = true;

         LOG.info("Reading until client send DISCONN or closes the connection...");
         while ((shouldRun) && (line = in.readLine()) != null) {
            ProtocolCommands command = ProtocolCommands.getCommandFromMessage(line);
            if (command == null) {
               out.print("CMD_ERR\n");
            } else {
               switch (command) {
                  case ping:
                     if (serverType.equals("good")) {
                        out.print(ProtocolCommands.ping.getMessageConfirmation());
                        LOG.info(ProtocolCommands.ping.getMessageConfirmation());
                     } else {
                        out.print("PINGG");
                        LOG.info("PINGG");
                     }
                     break;
                  case forward:
                     if (serverType.equals("good")) {
                        out.print(ProtocolCommands.forward.getMessageConfirmation());
                        LOG.info(ProtocolCommands.forward.getMessageConfirmation());
                     } else {
                        out.print("FWD_KO");
                        LOG.info("FWD_KO");
                     }
                     break;
                  case backward:
                     if (serverType.equals("good")) {
                        out.print(ProtocolCommands.backward.getMessageConfirmation());
                        LOG.info(ProtocolCommands.backward.getMessageConfirmation());
                     } else {
                        out.print("BKWD_KO");
                        LOG.info("BKWD_KO");
                     }
                     break;
                  case rotateLeft:
                     if (serverType.equals("good")) {
                        out.print(ProtocolCommands.rotateLeft.getMessageConfirmation());
                        LOG.info(ProtocolCommands.rotateLeft.getMessageConfirmation());
                     } else {
                        out.print("ROTATE_LEFT_KO");
                        LOG.info("ROTATE_LEFT_KO");
                     }
                     break;
                  case rotateRight:
                     if (serverType.equals("good")) {
                        out.print(ProtocolCommands.rotateRight.getMessageConfirmation());
                        LOG.info(ProtocolCommands.rotateRight.getMessageConfirmation());
                     } else {
                        out.print("ROTATE_RIGHT_KO");
                        LOG.info("ROTATE_RIGHT_KO");
                     }
                     break;
                  case frontleft:
                     if (serverType.equals("good")) {
                        out.print(ProtocolCommands.frontleft.getMessageConfirmation());
                        LOG.info(ProtocolCommands.frontleft.getMessageConfirmation());
                     } else {
                        out.print("FRONT_L_KO");
                        LOG.info("FRONT_L_KO");
                     }
                     break;
                  case frontRight:
                     if (serverType.equals("good")) {
                        out.print(ProtocolCommands.frontRight.getMessageConfirmation());
                        LOG.info(ProtocolCommands.frontRight.getMessageConfirmation());
                     } else {
                        out.print("FRONT_R_KO");
                        LOG.info("FRONT_R_KO");
                     }
                     break;
                  case backwardsRight:
                     if (serverType.equals("good")) {
                        out.print(ProtocolCommands.backwardsRight.getMessageConfirmation());
                        LOG.info(ProtocolCommands.backwardsRight.getMessageConfirmation());
                     } else {
                        out.print("BCK_R_KO");
                        LOG.info("BCK_R_KO");
                     }
                     break;
                  case backwardsLeft:
                     if (serverType.equals("good")) {
                        out.print(ProtocolCommands.backwardsLeft.getMessageConfirmation());
                        LOG.info(ProtocolCommands.backwardsLeft.getMessageConfirmation());
                     } else {
                        out.print("BCK_L_KO");
                        LOG.info("BCK_L_KO");
                     }
                     break;
                  case stop:
                     if (serverType.equals("good")) {
                        out.print(ProtocolCommands.stop.getMessageConfirmation());
                        LOG.info(ProtocolCommands.stop.getMessageConfirmation());
                        // To stop the server used in ClientGoodServerTest
                        // The cli.stop() func isn't used in these test
                        if (testRun) {
                           this.stop();
                           return;
                        }
                     } else {
                        out.print("STOPP");
                        LOG.info("STOPP");
                     }
                     break;
                  case disconnect:
                     out.print(ProtocolCommands.disconnect.getMessageConfirmation());
                     shouldRun = false;
                     if (serverType.equals("bad"))
                     // To stop the server used in ClientBadServerTest.
                     // cli.dissconnect() is run only once after all tests completed.
                     { this.stop(); }
               }
               out.print("\n");
            }
            out.flush();
         }
      }
   }

   private void stop() {
      try {
         LOG.log(Level.INFO, "Stop {0} server ...", serverType);
         if (clientSocket != null) {
            clientSocket.close();
            in.close();
            out.close();
         }
         serverSocket.close();
         pictureServer.stop();
      } catch (IOException e) {
      }
   }

   /**
    * A server that handles the picture side of the robot. It also has the same behaviour as the main class Server in
    * case the attribute serverType equals "bad"
    */
   class PictureServer implements Runnable {
      final Logger LOG = Logger.getLogger(Server.class.getName());
      private final int PORT = 2026;
      private ServerSocket serverSocket;
      private Socket clientSocket = null;
      private BufferedReader in = null;
      private PrintWriter out = null;
      private BufferedOutputStream imageOut = null;
      private boolean running = true;

      public void stop() throws IOException {
         LOG.log(Level.INFO, "Stop picture server ...");
         if (clientSocket != null) {
            clientSocket.close();
            in.close();
            out.close();
         }
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

      private void start() throws IOException {
         LOG.log(Level.INFO, "Start picture server ...");
         serverSocket = new ServerSocket(PORT);
      }

      /**
       * Sends a picture to the connecting client. The picture is always the same
       *
       * @throws IOException
       */
      private void listen() throws IOException {
         while (true) {
            LOG.log(Level.INFO, "Waiting (blocking) for a new client on port {0}", PORT);
            try {
               clientSocket = serverSocket.accept();
            } catch (SocketException e){

            }
            if (!running) {
               break;
            }
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream());
            imageOut = new BufferedOutputStream(clientSocket.getOutputStream());
            String message = in.readLine();
            LOG.log(Level.INFO, "Received first message from client {0}", message);
            if (!message.equals("PICTURE")) {
               out.print("CMD_ERR\n");
               out.flush();
               in.close();
               out.close();
               imageOut.close();
               clientSocket.close();
               continue;
            }
            if (serverType.equals("good")) {
               out.print("PICTURE_OK\n");
               out.flush();
               String response;
               do {
                  LOG.log(Level.INFO, "Sending a picture...");
                  BufferedImage bi = ImageIO.read(getClass().getClassLoader().getResource("logo.png"));
                  ImageIO.write(bi, "png", imageOut);
                  imageOut.flush();
                  response = in.readLine();
               } while (!response.equals("RECEIVED_OK"));
            } else {
               out.print("PICTURE_KO\n");
               out.flush();
            }
            in.close();
            out.close();
            imageOut.close();
            clientSocket.close();
         }
      }
   }
}
