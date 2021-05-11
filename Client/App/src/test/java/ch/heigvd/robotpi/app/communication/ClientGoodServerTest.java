package ch.heigvd.robotpi.app.communication;

import ch.heigvd.robotpi.servertest.Server;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This class test the client with a server that respond what is expected in protocol
 */
class ClientGoodServerTest {

   private static Client cli = new Client();

   @BeforeAll
   static void beforeAll() {
      Thread srvThread = new Thread(new Server(2025, "good", true));
      srvThread.start();
      try {
         // To be sure that the server is running (tests on github)
         Thread.sleep(2000);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }

   @AfterAll
   static void afterAll() {
      try {
         cli.connect("127.0.0.1");
         // The stop() func is used here to stop the server
         cli.stop();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   @BeforeEach
   void setUp() {

   }

   @AfterEach
   void teardown() {

   }

   @Test
   void isConnectedReturnTrueWhenConnected() {
      boolean result = false;
      try {
         cli.connect("127.0.0.1");
         result = cli.isConnected();
         cli.disconnect();
      } catch (Exception e) {
         e.printStackTrace();
      }

      boolean expected = true;
      assertEquals(expected, result);
   }

   @Test
   void isConnectedReturnFalseWhenNotConnected() { // TODO A revoir
      // To get the default value FALSE
      boolean result = cli.isConnected();

      try {
         // throws timout exception because wrong ip
         cli.connect("192.22.22.22");
         // not updated here
         result = cli.isConnected();
      } catch (Exception e) {
         e.printStackTrace();
      }

      boolean expected = false;
      assertEquals(expected, result);
   }

   @Test
   void disconnectWorks() {
      assertThrows(IOException.class, () -> {
         cli.connect("127.0.0.1");
         if (cli.isConnected()) {
            cli.disconnect();
             if (!cli.isConnected()) { cli.goRight(); }
         }
      });
   }

}


