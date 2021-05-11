package ch.heigvd.robotpi.communication;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class test the client with a server that doesn't respond what is expected in protocol
 */
class ClientBadServerTest {
    private static Client cli = new Client();

    @BeforeAll
    static void beforeAll() {
        Thread srvThread = new Thread(new ClientGoodServerTest.Server(2025, "bad"));
        srvThread.start();
        try {
            // To be sure that the server is running (tests on github)
            Thread.sleep(2000);
            cli.connect("127.0.0.1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void teardown() {
    }

    @AfterAll
    static void afterAll() {
        try {
            cli.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void pingTrowsException() {
        assertThrows(Client.LostConnectionException.class,
                () -> {
                    cli.ping();
                });
    }

    @Test
    void goForwardTrowsException() {
        assertThrows(Client.RobotException.class,
                () -> {
                    cli.goForward();
                });
    }

    @Test
    void goBackwardTrowsException() {
        assertThrows(Client.RobotException.class,
                () -> {
                    cli.goBackward();
                });
    }

    @Test
    void goLeftTrowsException() {
        assertThrows(Client.RobotException.class,
                () -> {
                    cli.goLeft();
                });
    }

    @Test
    void goRightTrowsException() {
        assertThrows(Client.RobotException.class,
                () -> {
                    cli.goRight();
                });
    }

    @Test
    void stopTrowsException() {
        assertThrows(Client.RobotException.class,
                () -> {
                    cli.stop();
                });
    }

    // TODO goFrontLeft and next
}
