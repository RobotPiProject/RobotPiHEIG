/*
 * @File Client.java
 * @Authors : Jade Gröli
 * @Date 18 mars 2021
 */
package ch.heigvd.robotpi.app.communication;

import ch.heigvd.robotpi.servertest.ProtocolCommands;
import lombok.Getter;

import javax.imageio.ImageIO;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import javax.net.ssl.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.util.HashSet;
import java.util.Set;

/**
 * The type Client.
 */
public class Client {
   private static final String[] PROTOCOLS = new String[]{"TLSv1.3"};
   private static final String[] CIPHER_SUITES = new String[]{"TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384"};
   /**
    * The Port uses for the communication.
    */
   public final int PORT = 2025;
   /**
    * The Portpicture uses to transfer picture
    */
   public final int PORTPICTURE = 2026;
   private SSLSocket clientSocket = null;
   private String ipAddress;
   private PrintWriter out;
   private BufferedReader in;
   private boolean isConnected;
   private boolean isMoving = false;

   /**
    * Connects the client to the server of the pi robot
    *
    * @param ip l'adresse ip du robot pi
    *
    * @throws CantConnectException     , connexion didn't work on server side
    * @throws IOException              problem with socket on client side
    * @throws IncorrectDeviceException ip address does not match a pi robot
    */
   public void connect(String ip) throws CantConnectException, IOException, IncorrectDeviceException {
      try {
         this.ipAddress = ip;
         this.clientSocket = createSocket(ip, PORT);
         out = new PrintWriter(clientSocket.getOutputStream(), true);
         in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

         printSocketInfo(clientSocket);
         clientSocket.startHandshake();


         isConnected = true;

         out.print(ProtocolCommands.conn.getMessage());
         out.print("\n");
         out.flush();

         String message = in.readLine();


         if (message.equals("CONN_ERR")) {
            clientSocket.close();
            throw new CantConnectException();
         } else if (!message.equals(ProtocolCommands.conn.getMessageConfirmation())) {
            clientSocket.close();
            throw new IncorrectDeviceException();
         }
      } catch (Exception e) {
         System.err.println(e.toString());
         throw new CantConnectException();
      }
   }

   /**
    * Sends a request to the server to fetch a picture taken by the pi robot
    *
    * @param imagename the path of the image
    *
    * @throws CantConnectException the cant connect exception
    * @throws RobotException       the robot exception
    * @throws PictureTransferError the picture transfer error
    * @throws IOException          the io exception
    */
   public void takePicture(String imagename)
           throws CantConnectException, RobotException, PictureTransferError, IOException {
      if (!isConnected) {
         throw new CantConnectException();
      }

      PrintWriter outPic = null;
      BufferedReader inPic = null;
      SSLSocket socketPicture = null;
      try {
         socketPicture = createSocket(ipAddress, PORTPICTURE);

         outPic = new PrintWriter(socketPicture.getOutputStream(), true);
         inPic = new BufferedReader(new InputStreamReader(socketPicture.getInputStream()));

         printSocketInfo(socketPicture);
         socketPicture.startHandshake();

      } catch (Exception e) {
         System.err.println(e.toString());
         throw new CantConnectException();
      }

         outPic.print("PICTURE\n");
         outPic.flush();
         String message = inPic.readLine();

         if (!message.equals("PICTURE_OK")) {
            throw new RobotException();
         }

         InputStream is = socketPicture.getInputStream();


         BufferedImage bi;
         try {
            bi = ImageIO.read(is);
         } catch (IOException e) {
            throw new PictureTransferError();
         }

         socketPicture.close();

         ImageIO.write(bi, "jpg", new File(imagename));

   }

   /**
    * Launch service discovery.
    *
    * @return the set of ip addresses discovered that have the correct type
    *
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

         jmdns.removeServiceListener("_robopi._tcp.local.", sampleListener);
         jmdns.close();

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
         out.print(ProtocolCommands.disconnect.getMessage());
         out.print("\n");
         out.flush();
         message = in.readLine();
      } while (!message.equals(ProtocolCommands.disconnect.getMessageConfirmation()) && count++ != 5);
      in.close();
      out.close();
      clientSocket.close();
      if (message.equals(ProtocolCommands.disconnect.getMessageConfirmation())) {
         isConnected = false;
      }

   }

   /**
    * Ping the server and expects an answer.
    *
    * @throws IOException             the io exception
    * @throws LostConnectionException the connexion is lost
    */
   public void ping() throws IOException, LostConnectionException {
      out.print(ProtocolCommands.ping.getMessage());
      out.print("\n");
      out.flush();
      if (!in.readLine().equals(ProtocolCommands.ping.getMessage())) {
         isConnected = false;
         in.close();
         out.close();
         clientSocket.close();
         throw new LostConnectionException();
      }
   }

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
      out.print(ProtocolCommands.forward.getMessage());
      out.print("\n");
      out.flush();
      if (!in.readLine().equals(ProtocolCommands.forward.getMessageConfirmation())) {
         throw new RobotException();
      }
      isMoving = true;
   }

   //TODO catch les ioException et throw les bonnes exc

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
      out.print(ProtocolCommands.backward.getMessage());
      out.print("\n");
      out.flush();
      if (!in.readLine().equals(ProtocolCommands.backward.getMessageConfirmation())) {
         throw new RobotException();
      }
      isMoving = true;
   }

   //lancer des exception dans le cas ou serveur ne reagit pas comme prevu

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
      out.print(ProtocolCommands.rotateLeft.getMessage());
      out.print("\n");
      out.flush();
      if (!in.readLine().equals(ProtocolCommands.rotateLeft.getMessageConfirmation())) {
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
      out.print(ProtocolCommands.rotateRight.getMessage());
      out.print("\n");
      out.flush();
      if (!in.readLine().equals(ProtocolCommands.rotateRight.getMessageConfirmation())) {
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
      out.print(ProtocolCommands.stop.getMessage());
      out.print("\n");
      out.flush();
      if (!in.readLine().equals(ProtocolCommands.stop.getMessageConfirmation())) {
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
      out.print(ProtocolCommands.frontleft.getMessage());
      out.print("\n");
      out.flush();
      if (!in.readLine().equals(ProtocolCommands.frontleft.getMessageConfirmation())) {
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
      out.print(ProtocolCommands.frontRight.getMessage());
      out.print("\n");
      out.flush();
      if (!in.readLine().equals(ProtocolCommands.frontRight.getMessageConfirmation())) {
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
      out.print(ProtocolCommands.backwardsRight.getMessage());
      out.print("\n");
      out.flush();
      if (!in.readLine().equals(ProtocolCommands.backwardsRight.getMessageConfirmation())) {
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
      out.print(ProtocolCommands.backwardsLeft.getMessage());
      out.print("\n");
      out.flush();
      if (!in.readLine().equals(ProtocolCommands.backwardsLeft.getMessageConfirmation())) {
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
    * Create SSL context
    * @return SSLContext
    * @throws Exception
    */
   private static SSLContext initTLS() throws Exception {
      // TrustManagerFactory ()
      KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
      String password = "robotpi";
      TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      InputStream inputStream1 = Client.class.getClassLoader().getResourceAsStream("rpTrustStore.jts");
      trustStore.load(inputStream1, password.toCharArray());
      trustManagerFactory.init(trustStore);
      X509TrustManager x509TrustManager = null;

      for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) {
         if (trustManager instanceof X509TrustManager) {
            x509TrustManager = (X509TrustManager) trustManager;
            break;
         }
      }

      if (x509TrustManager == null) throw new NullPointerException();

      // set up the SSL Context
      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, new TrustManager[]{x509TrustManager}, null);

      return sslContext;
   }

   /**
    * Create SSL socket
    * @param host IP
    * @param port port
    * @return SSLSocket
    * @throws IOException
    */
   private static SSLSocket createSocket(String host, int port) throws Exception {
      SSLContext sslContext = initTLS();
      SSLSocketFactory socketFactory = sslContext.getSocketFactory();
      SSLSocket socket = (SSLSocket) socketFactory.createSocket(host, port);
      socket.setEnabledProtocols(PROTOCOLS);
      socket.setEnabledCipherSuites(socket.getSupportedCipherSuites());

      return socket;
   }

   private static void printSocketInfo(SSLSocket s) {
      System.out.println("Socket class: " + s.getClass());
      System.out.println("   Remote address = " + s.getInetAddress().toString());
      System.out.println("   Remote port = " + s.getPort());
      System.out.println("   Local socket address = " + s.getLocalSocketAddress().toString());
      System.out.println("   Local address = " + s.getLocalAddress().toString());
      System.out.println("   Local port = " + s.getLocalPort());
      System.out.println("   Need client authentication = " + s.getNeedClientAuth());
      SSLSession ss = s.getSession();
      System.out.println("   Cipher suite = " + ss.getCipherSuite());
      System.out.println("   Protocol = " + ss.getProtocol());
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
      // par ex si robot envoi mauvaise réponse, pb cote robot en general
   }

   /**
    * The type Picture transfer error.
    */
   public class PictureTransferError extends CommException {
      // la photo n'a pas été reçue côté client, l'utilisateur doit la redemander
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
