package ch.heigvd.robotpi.app.communication;

import ch.heigvd.robotpi.servertest.Server;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This class test the client with a server that doesn't respond what is expected in protocol
 */
class ClientBadServerTest {
   private static Client cli = new Client();

   @BeforeAll
   static void beforeAll() {
      Thread srvThread = new Thread(new Server(2025, "bad", true));
      srvThread.start();
      try {
         // To be sure that the server is running (tests on github)
         Thread.sleep(2000);
         cli.connect("127.0.0.1");
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   @AfterAll
   static void afterAll() {
      try {
         cli.disconnect();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   @AfterEach
   void teardown() {
   }

   @Test
   void pingThrowsException() throws Client.CantConnectException, IOException, Client.IncorrectDeviceException {
      assertThrows(Client.LostConnectionException.class, () -> {
         cli.ping();
      });
      cli.connect("127.0.0.1");
   }

   @Test
   void pictureThrowsException() {
      assertThrows(Client.RobotException.class, () -> {
         cli.takePicture(new File(".").getCanonicalPath() + "/figures/imageTest");
      });
   }

   @Test
   void goForwardThrowsException() {
      assertThrows(Client.RobotException.class, () -> {
         cli.goForward();
      });
   }

   @Test
   void goBackwardThrowsException() {
      assertThrows(Client.RobotException.class, () -> {
         cli.goBackward();
      });
   }

   @Test
   void goLeftThrowsException() {
      assertThrows(Client.RobotException.class, () -> {
         cli.goLeft();
      });
   }

   @Test
   void goRightThrowsException() {
      assertThrows(Client.RobotException.class, () -> {
         cli.goRight();
      });
   }

   @Test
   void stopThrowsException() {
      assertThrows(Client.RobotException.class, () -> {
         cli.stop();
      });

   }

   @Test
   void goFrontLeftThrowsException() {
      assertThrows(Client.RobotException.class, () -> {
         cli.goFrontLeft();
      });
   }

   @Test
   void goFrontRightThrowsException() {
      assertThrows(Client.RobotException.class, () -> {
         cli.goFrontRight();
      });
   }

   @Test
   void goBackwardsRightThrowsException() {
      assertThrows(Client.RobotException.class, () -> {
         cli.goBackwardsRight();
      });
   }

   @Test
   void goBackwardsLeftThrowsException() {
      assertThrows(Client.RobotException.class, () -> {
         cli.goBackwardsLeft();
      });
   }

}
