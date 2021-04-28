package ch.heigvd.robotpi.communication;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean isConnected;
    public final int PORT = 2025;

    public void connect(String ip) throws CantConnectException, IOException, IncorrectDeviceException {
        clientSocket = new Socket(ip, PORT);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        isConnected = true;
        out.println("CONN");
        String message = in.readLine();
        if (message.equals("CONN_ERR")) {
            clientSocket.close();
            throw new CantConnectException();
        } else if (!message.equals("CONN_OK")) {
            clientSocket.close();
            throw new IncorrectDeviceException();
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void disconnect() throws IOException {
        int count = 1;
        String message;
        do {
            out.println("DISCONN");
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
        out.println("PING");
        if (!in.readLine().equals("PING")) {
            throw new LostConnectionException();
        }
    }

    //lancer des exception dans le cas ou serveur ne reagit pas comme prevu

    public void goForward() throws RobotException, IOException {
        out.println("FWD");
        if (!in.readLine().equals("FWD_OK")) {
            throw new RobotException();
        }
        isMoving = true;
    }

    public void goBackward() throws IOException, RobotException {
        out.println("BKWD");
        if (!in.readLine().equals("BKWD_OK")) {
            throw new RobotException();
        }
        isMoving = true;
    }

    public void goLeft() throws IOException, RobotException {
        out.println("ROTATE_LEFT");
        if (!in.readLine().equals("ROTATE_LEFT_OK")) {
            throw new RobotException();
        }
        isMoving = true;
    }

    public void goRight() throws IOException, RobotException {
        out.println("ROTATE_RIGHT");
        if (!in.readLine().equals("ROTATE_RIGHT_OK")) {
            throw new RobotException();
        }
        isMoving = true;
    }

    public void stop() throws IOException, RobotException {
        out.println("STOP");
        if (!in.readLine().equals("STOP")) {
            throw new RobotException();
        }
        isMoving = false;
    }

    //TODO : a voir avec le protocole pour ces méthodes et la classe interne d'erreur
    public void goFrontLeft() throws IOException, RobotException {
        out.println("FRONT_L");
        if (!in.readLine().equals("FRONT_L_OK")) {
            throw new RobotException();
        }
        isMoving = true;
    }

    public void goFrontRight() throws RobotException, IOException {
        out.println("RIGHT_R");
        if (!in.readLine().equals("RIGHT_R_OK")) {
            throw new RobotException();
        }
        isMoving = true;
    }

    public void goBackwardsRight() throws IOException, RobotException {
        out.println("BCK_R");
        if (!in.readLine().equals("BCK_R_OK")) {
            throw new RobotException();
        }
        isMoving = true;
    }

    public void goBackwardsLeft() throws IOException, RobotException {
        out.println("BCK_L");
        if (!in.readLine().equals("BCK_L_OK")) {
            throw new RobotException();
        }
        isMoving = true;
    }

    public class CommException extends Exception {
    }

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

    public boolean isMoving() {
        return isMoving;
    }

    private boolean isMoving = false;

}
