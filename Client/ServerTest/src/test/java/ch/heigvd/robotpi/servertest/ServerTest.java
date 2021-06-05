package ch.heigvd.robotpi.servertest;/*
 * @File ServerTest.java
 * @Authors : David González León
 * @Date 12 mai 2021
 */

import org.junit.jupiter.api.Test;

import javax.net.ssl.*;
import java.io.*;
import java.net.Socket;
import java.security.KeyStore;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ServerTest {

   private void closeEverything(Server server, SSLSocket socket, BufferedReader in, PrintWriter out) throws IOException {
      socket.close();
      in.close();
      out.close();
      server.stopExecution();
   }

   /**
    * Create SSL context
    * @return SSLContext
    * @throws Exception
    */
   private SSLContext initTLS() throws Exception {
            // TrustManagerFactory ()
      KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
      String password = "robotpi";
      TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      InputStream inputStream1 = getClass().getClassLoader().getResourceAsStream("rpTrustStore.jts");
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
    private SSLSocket createSocket(String host, int port) throws Exception {
       SSLContext sslContext = initTLS();
       SSLSocketFactory socketFactory = sslContext.getSocketFactory();
       SSLSocket socket = (SSLSocket) socketFactory.createSocket(host, port);

       socket.setEnabledProtocols(new String[]{"TLSv1.3"});
       socket.setEnabledCipherSuites(socket.getSupportedCipherSuites());

        return socket;
    }

   @Test
   void testConnectionGoodServer() throws Exception {
      Server server = new Server("good", true);
      Thread thread = new Thread(server);
      thread.start();
      try {
         Thread.sleep(2000);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }

      SSLSocket socket = createSocket("localhost", 2025);
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

      socket.startHandshake();

      out.println(ProtocolCommands.conn.getMessage());
      assertEquals(ProtocolCommands.conn.getMessageConfirmation(), in.readLine());

      closeEverything(server, socket, in, out);
   }

   @Test
   void testCommandsWorksGoodServer() throws Exception {
      Server server = new Server("good", true);
      Thread thread = new Thread(server);
      thread.start();
      try {
         Thread.sleep(2000);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }

      SSLSocket socket = createSocket("localhost", 2025);
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

      socket.startHandshake();

      out.println(ProtocolCommands.conn.getMessage());
      assertEquals(ProtocolCommands.conn.getMessageConfirmation(), in.readLine());

      out.println(ProtocolCommands.forward.getMessage());
      assertEquals("FWD_OK", in.readLine());
      out.println(ProtocolCommands.backward.getMessage());
      assertEquals("BKWD_OK", in.readLine());
      out.println(ProtocolCommands.rotateLeft.getMessage());
      assertEquals("ROTATE_LEFT_OK", in.readLine());
      out.println(ProtocolCommands.rotateRight.getMessage());
      assertEquals("ROTATE_RIGHT_OK", in.readLine());
      out.println(ProtocolCommands.frontleft.getMessage());
      assertEquals("FRONT_L_OK", in.readLine());
      out.println(ProtocolCommands.frontRight.getMessage());
      assertEquals("FRONT_R_OK", in.readLine());
      out.println(ProtocolCommands.backwardsLeft.getMessage());
      assertEquals("BCK_L_OK", in.readLine());
      out.println(ProtocolCommands.backwardsRight.getMessage());
      assertEquals("BCK_R_OK", in.readLine());

      closeEverything(server, socket, in, out);
   }

   @Test
   void testDisconnWorks() throws Exception {
      Server server = new Server("good", true);
      Thread thread = new Thread(server);
      thread.start();
      try {
         Thread.sleep(2000);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }

      SSLSocket socket = createSocket("localhost", 2025);
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

      socket.startHandshake();

      out.println(ProtocolCommands.conn.getMessage());
      assertEquals(ProtocolCommands.conn.getMessageConfirmation(), in.readLine());

      out.println(ProtocolCommands.disconnect.getMessage());
      assertEquals(ProtocolCommands.disconnect.getMessageConfirmation(), in.readLine());

      in.close();
      out.close();
      socket.close();

      socket = createSocket("localhost", 2025);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out = new PrintWriter(socket.getOutputStream(), true);

      socket.startHandshake();

      out.println(ProtocolCommands.conn.getMessage());
      assertEquals(ProtocolCommands.conn.getMessageConfirmation(), in.readLine());

      closeEverything(server, socket, in, out);

   }

   @Test
   void testPingWorks() throws Exception {
      Server server = new Server("good", true);
      Thread thread = new Thread(server);
      thread.start();
      try {
         Thread.sleep(2000);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }

      SSLSocket socket = createSocket("localhost", 2025);
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

      socket.startHandshake();

      out.println(ProtocolCommands.conn.getMessage());
      assertEquals(ProtocolCommands.conn.getMessageConfirmation(), in.readLine());

      out.println(ProtocolCommands.ping.getMessage());
      assertEquals(ProtocolCommands.ping.getMessageConfirmation(), in.readLine());

      closeEverything(server, socket, in, out);
   }
}