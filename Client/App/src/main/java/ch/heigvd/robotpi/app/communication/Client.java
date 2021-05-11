package ch.heigvd.robotpi.app.communication;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket clientSocket;
    private String ipAddress;
    private PrintWriter out;
    private BufferedReader in;
    private boolean isConnected;
    private boolean isMoving = false;
    public final int PORT = 2025;
    public final int PORTPICTURE = 2026;


    public void connect(String ip) throws CantConnectException, IOException, IncorrectDeviceException {
        clientSocket = new Socket(ip, PORT);
        ipAddress = ip;
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

    public void takePicture(String imagename) throws CantConnectException, IOException, RobotException {
        if (!isConnected) {
            throw new CantConnectException();
        }



        Socket socketPicture = new Socket(ipAddress, PORTPICTURE);

        PrintWriter outPic = new PrintWriter(socketPicture.getOutputStream(), true);
        BufferedReader inPic = new BufferedReader(new InputStreamReader(socketPicture.getInputStream()));

        outPic.println("PICTURE");
        String message = inPic.readLine();

        if (!message.equals("PICTURE_OK")) {
            throw new RobotException();
        }

        InputStream is = socketPicture.getInputStream();
        Scanner reader = new Scanner(is);
        System.out.print(reader.nextLine());
        BufferedImage bi;

        while (true) {
            try {
                bi = ImageIO.read(is);
                outPic.println("RECEIVED_OK");
                break;
            } catch (IOException e) {
                outPic.println("RESEND_PICTURE");
            }
        }

        socketPicture.close();

        ImageIO.write(bi, "jpg", new File(imagename));

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
            isConnected = false;
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
        if (!in.readLine().equals("STOP_OK")) {
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



}
