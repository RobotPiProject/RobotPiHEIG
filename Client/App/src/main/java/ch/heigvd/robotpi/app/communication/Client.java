package ch.heigvd.robotpi.app.communication;

import lombok.Getter;

import javax.imageio.ImageIO;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

/**
 * The type Client.
 */
public class Client {
   /**
    * The Port uses for the communication.
    */
   public final int PORT = 2025;
   /**
    * The Portpicture uses to transfer picture
    */
   public final int PORTPICTURE = 2026;
   private Socket clientSocket;
   private String ipAddress;
   private PrintWriter out;
   private BufferedReader in;
   private boolean isConnected;
   private boolean isMoving = false;

   /**
    * Connects the client to the server of the pi robot
    *
    * @param ip l'adresse ip du robot pi
    * @throws CantConnectException     , connexion didn't work on server side
    * @throws IOException              problem with socket on client side
    * @throws IncorrectDeviceException ip address does not match a pi robot
    */
   public void connect(String ip) throws CantConnectException, IOException, IncorrectDeviceException {
      clientSocket = new Socket(ip, PORT);
      ipAddress = ip;
      out = new PrintWriter(clientSocket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      isConnected = true;
      out.print("CONN\n");
      out.flush();
      String message = in.readLine();
      if (message.equals("CONN_ERR")) {
         clientSocket.close();
         throw new CantConnectException();
      } else if (!message.equals("CONN_OK")) {
         clientSocket.close();
         throw new IncorrectDeviceException();
      }
   }

   /**
    * Sends a request to the server to fetch a picture taken by the pi robot
    *
    * @param imagename the path of the image
    * @throws CantConnectException connexion didn't work on server side
    * @throws IOException          problem with socket on client side
    * @throws RobotException       an error on pi robot side occurred
    */
   public void takePicture(String imagename) throws CantConnectException, IOException, RobotException {
      if (!isConnected) {
         throw new CantConnectException();
      }

      Socket socketPicture = new Socket(ipAddress, PORTPICTURE);

      PrintWriter outPic = new PrintWriter(socketPicture.getOutputStream(), true);
      BufferedReader inPic = new BufferedReader(new InputStreamReader(socketPicture.getInputStream()));

      outPic.print("PICTURE\n");
      outPic.flush();
      String message = inPic.readLine();

      if (!message.equals("PICTURE_OK")) {
         throw new RobotException();
      }

      InputStream is = socketPicture.getInputStream();
      BufferedImage bi;

      while (true) {
         try {
            bi = ImageIO.read(is);
            outPic.print("RECEIVED_OK\n");
            outPic.flush();
            break;
         } catch (IOException e) {
            outPic.print("RESEND_PICTURE\n");
            outPic.flush();
         }
      }

      socketPicture.close();

      ImageIO.write(bi, "jpg", new File(imagename));

   }

   /**
    * Launch service discovery set.
    *
    * @return the set of ip addresses discovered
    * @throws InterruptedException the interrupted exception
    */
   public Set<String> launchServiceDiscovery() throws InterruptedException {
      try {
         // Create a JmDNS instance
         JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());

         SampleListener sampleListener = new SampleListener();
         // Add a service listener
         jmdns.addServiceListener("_robopi._tcp.local.", sampleListener);

         // Wait a bit
         Thread.sleep(3000);

         return sampleListener.getAddresses();

      } catch (UnknownHostException e) {
         System.out.println(e.getMessage());
      } catch (IOException e) {
         System.out.println(e.getMessage());
      }
      return null;
   }

   /**
    * getter of boolean which indicates if the client is connected to the server of not
    *
    * @return true if it's connected, false if not
    */
   public boolean isConnected() {
      return isConnected;
   }

   /**
    * Disconnect the client from the server
    *
    * @throws IOException the io exception
    */
   public void disconnect() throws IOException {
      int count = 1;
      String message;
      do {
         out.print("DISCONN\n");
         out.flush();
         message = in.readLine();
      } while (!message.equals("DISCONN_OK") && count++ != 5);
      in.close();
      out.close();
      clientSocket.close();
      if (message.equals("DISCONN_OK")) {
         isConnected = false;
      }

   }

   //TODO catch les ioException et throw les bonnes exc

   /**
    * Ping the server and expects an answer.
    *
    * @throws IOException             the io exception
    * @throws LostConnectionException the connexion is lost
    */
   public void ping() throws IOException, LostConnectionException {
      out.print("PING\n");
      out.flush();
      if (!in.readLine().equals("PING")) {
         isConnected = false;
         in.close();
         out.close();
         clientSocket.close();
         throw new LostConnectionException();
      }
   }

   //lancer des exception dans le cas ou serveur ne reagit pas comme prevu

   /**
    * Go forward.
    *
    * @throws RobotException       the robot exception
    * @throws IOException          the io exception
    * @throws CantConnectException the cant connect exception
    */
   public void goForward() throws RobotException, IOException, CantConnectException {
      if (!isConnected) {
         throw new CantConnectException();
      }
      out.print("FWD\n");
      out.flush();
      if (!in.readLine().equals("FWD_OK")) {
         throw new RobotException();
      }
      isMoving = true;
   }

   /**
    * Go backward.
    *
    * @throws IOException          the io exception
    * @throws RobotException       the robot exception
    * @throws CantConnectException the cant connect exception
    */
   public void goBackward() throws IOException, RobotException, CantConnectException {
      if (!isConnected) {
         throw new CantConnectException();
      }
      out.print("BKWD\n");
      out.flush();
      if (!in.readLine().equals("BKWD_OK")) {
         throw new RobotException();
      }
      isMoving = true;
   }

   /**
    * Go left.
    *
    * @throws IOException          the io exception
    * @throws RobotException       the robot exception
    * @throws CantConnectException the cant connect exception
    */
   public void goLeft() throws IOException, RobotException, CantConnectException {
      if (!isConnected) {
         throw new CantConnectException();
      }
      out.print("ROTATE_LEFT\n");
      out.flush();
      if (!in.readLine().equals("ROTATE_LEFT_OK")) {
         throw new RobotException();
      }
      isMoving = true;
   }

   /**
    * Go right.
    *
    * @throws IOException          the io exception
    * @throws RobotException       the robot exception
    * @throws CantConnectException the cant connect exception
    */
   public void goRight() throws IOException, RobotException, CantConnectException {
      if (!isConnected) {
         throw new CantConnectException();
      }
      out.print("ROTATE_RIGHT\n");
      out.flush();
      if (!in.readLine().equals("ROTATE_RIGHT_OK")) {
         throw new RobotException();
      }
      isMoving = true;
   }

   /**
    * Stop.
    *
    * @throws IOException          the io exception
    * @throws RobotException       the robot exception
    * @throws CantConnectException the cant connect exception
    */
   public void stop() throws IOException, RobotException, CantConnectException {
      if (!isConnected) {
         throw new CantConnectException();
      }
      out.print("STOP\n");
      out.flush();
      if (!in.readLine().equals("STOP_OK")) {
         throw new RobotException();
      }
      isMoving = false;
   }

   /**
    * Go front left.
    *
    * @throws IOException          the io exception
    * @throws RobotException       the robot exception
    * @throws CantConnectException the cant connect exception
    */
//TODO : a voir avec le protocole pour ces méthodes et la classe interne d'erreur
   public void goFrontLeft() throws IOException, RobotException, CantConnectException {
      if (!isConnected) {
         throw new CantConnectException();
      }
      out.print("FRONT_L\n");
      out.flush();
      if (!in.readLine().equals("FRONT_L_OK")) {
         throw new RobotException();
      }
      isMoving = true;
   }

   /**
    * Go front right.
    *
    * @throws RobotException       the robot exception
    * @throws IOException          the io exception
    * @throws CantConnectException the cant connect exception
    */
   public void goFrontRight() throws RobotException, IOException, CantConnectException {
      if (!isConnected) {
         throw new CantConnectException();
      }
      out.print("FRONT_R\n");
      out.flush();
      if (!in.readLine().equals("FRONT_R_OK")) {
         throw new RobotException();
      }
      isMoving = true;
   }

   /**
    * Go backwards right.
    *
    * @throws IOException          the io exception
    * @throws RobotException       the robot exception
    * @throws CantConnectException the cant connect exception
    */
   public void goBackwardsRight() throws IOException, RobotException, CantConnectException {
      if (!isConnected) {
         throw new CantConnectException();
      }
      out.print("BCK_R\n");
      out.flush();
      if (!in.readLine().equals("BCK_R_OK")) {
         throw new RobotException();
      }
      isMoving = true;
   }

   /**
    * Go backwards left.
    *
    * @throws IOException          the io exception
    * @throws RobotException       the robot exception
    * @throws CantConnectException the cant connect exception
    */
   public void goBackwardsLeft() throws IOException, RobotException, CantConnectException {
      if (!isConnected) {
         throw new CantConnectException();
      }
      out.print("BCK_L\n");
      out.flush();
      if (!in.readLine().equals("BCK_L_OK")) {
         throw new RobotException();
      }
      isMoving = true;
   }

   /**
    * Is moving boolean.
    *
    * @return the boolean
    */
   public boolean isMoving() {
      return isMoving;
   }

   /**
    * The type Comm exception.
    */
   public class CommException extends Exception {}

   /**
    * The type Cant connect exception.
    */
   public class CantConnectException extends CommException {
      // pb connexion en general
   }

   /**
    * The type Incorrect device exception.
    */
   public class IncorrectDeviceException extends CommException {
      // qqn avec IP mais pas Robot pi
   }

   /**
    * The type Lost connection exception.
    */
   public class LostConnectionException extends CommException {
      //pb ping connexion
   }

   /**
    * The type Robot exception.
    */
   public class RobotException extends CommException {
      // par ex si robot envoi mauvaise reponse, pb cote robot en general
   }


   private class SampleListener implements ServiceListener {

      private final String NAME = "_robopi._tcp.local.";
      @Getter private Set<String> addresses = new HashSet<>();

      @Override
      public void serviceAdded(ServiceEvent event) {
         System.out.println("Service added: " + event.getInfo());
      }

      @Override
      public void serviceRemoved(ServiceEvent event) {
         System.out.println("Service removed: " + event.getInfo());
      }

      @Override
      public void serviceResolved(ServiceEvent event) {
         System.out.println("Service resolved: " + event.getInfo());
         if (event.getType().equals(NAME)) {
            InetAddress addr = event.getInfo().getInet4Addresses()[0];
            addresses.add(addr.getHostAddress());
         }
      }
   }
}
