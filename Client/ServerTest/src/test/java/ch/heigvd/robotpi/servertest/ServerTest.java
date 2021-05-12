package ch.heigvd.robotpi.servertest;/*
 * @File ServerTest.java
 * @Authors : David González León
 * @Date 12 mai 2021
 */

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ServerTest {

   private void closeEverything(Server server, Socket socket, BufferedReader in, PrintWriter out) throws IOException {
      socket.close();
      in.close();
      out.close();
      server.stopExecution();
   }

   @Test
   void testConnectionGoodServer() throws IOException {
      Server server = new Server(2025, "good", true);
      Thread thread = new Thread(server);
      thread.start();

      Socket socket = new Socket("localhost", 2025);
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
      out.println(ProtocolCommands.conn.getMessage());
      assertEquals(ProtocolCommands.conn.getMessageConfirmation(), in.readLine());

      closeEverything(server, socket, in, out);
   }

   @Test
   void testCommandsWorksGoodServer() throws IOException {
      Server server = new Server(2025, "good", true);
      Thread thread = new Thread(server);
      thread.start();

      Socket socket = new Socket("127.0.0.1", 2025);
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
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
   void testDisconnWorks() throws IOException {
      Server server = new Server(2025, "good", true);
      Thread thread = new Thread(server);
      thread.start();

      Socket socket = new Socket("127.0.0.1", 2025);
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
      out.println(ProtocolCommands.conn.getMessage());
      assertEquals(ProtocolCommands.conn.getMessageConfirmation(), in.readLine());

      out.println(ProtocolCommands.disconnect.getMessage());
      assertEquals(ProtocolCommands.disconnect.getMessageConfirmation(), in.readLine());

      in.close();
      out.close();
      socket.close();

      socket = new Socket("127.0.0.1", 2025);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out = new PrintWriter(socket.getOutputStream(), true);
      out.println(ProtocolCommands.conn.getMessage());
      assertEquals(ProtocolCommands.conn.getMessageConfirmation(), in.readLine());

      closeEverything(server, socket, in, out);

   }

   @Test
   void testPingWorks() throws IOException {
      Server server = new Server(2025, "good", true);
      Thread thread = new Thread(server);
      thread.start();

      Socket socket = new Socket("127.0.0.1", 2025);
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
      out.println(ProtocolCommands.conn.getMessage());
      assertEquals(ProtocolCommands.conn.getMessageConfirmation(), in.readLine());

      out.println(ProtocolCommands.ping.getMessage());
      assertEquals(ProtocolCommands.ping.getMessageConfirmation(), in.readLine());

      closeEverything(server, socket, in, out);
   }
}