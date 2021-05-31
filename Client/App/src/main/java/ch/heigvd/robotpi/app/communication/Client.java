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

public class Client {
   public final int PORT = 2025;
   public final int PORTPICTURE = 2026;
   private Socket clientSocket;
   private String ipAddress;
   private PrintWriter out;
   private BufferedReader in;
   private boolean isConnected;
   private boolean isMoving = false;

   /**
    * @brief connecte le client au serveur du robot pi
    * @param ip l'adresse ip du robot pi
    * @throws CantConnectException, la connection ne s'est pas faite correctement côté serveur
    * @throws IOException problème avec le socket côté client
    * @throws IncorrectDeviceException l'adresse ip ne correspond pas a un robot pi
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
    * @brief envoie une requete vers le serveur pour recuperer une photo prise par le robot pi
    * @param imagename, le nom ou le path de l'image
    * @throws CantConnectException
    * @throws IOException
    * @throws RobotException, un erreur du coté du robot est survenue
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

   public boolean isConnected() {
      return isConnected;
   }

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

   public boolean isMoving() {
      return isMoving;
   }

   public class CommException extends Exception {}

   public class CantConnectException extends CommException {
      // pb connexion en general
   }

   public class IncorrectDeviceException extends CommException {
      // qqn avec IP mais pas Robot pi
   }

   public class LostConnectionException extends CommException {
      //pb ping connexion
   }

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
