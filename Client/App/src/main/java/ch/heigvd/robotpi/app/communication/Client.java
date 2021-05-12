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
        out.print("CONN\n");
        out.flush();
        String message = in.readLine();
        if (message.equals("CONN_ERR\n")) {
            clientSocket.close();
            throw new CantConnectException();
        } else if (!message.equals("CONN_OK\n")) {
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

        outPic.print("PICTURE\n");
        outPic.flush();
        String message = inPic.readLine();

        if (!message.equals("PICTURE_OK\n")) {
            throw new RobotException();
        }

        InputStream is = socketPicture.getInputStream();
        Scanner reader = new Scanner(is);
        System.out.print(reader.nextLine());
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
        } while (!message.equals("DISCONN_OK\n") && count++ != 5);
        in.close();
        out.close();
        clientSocket.close();
        if (message.equals("DISCONN_OK\n")) {
            isConnected = false;
        }

    }

    //TODO catch les ioException et throw les bonnes exc

    public void ping() throws IOException, LostConnectionException {
        out.print("PING\n");
        out.flush();
        if (!in.readLine().equals("PING\n")) {
            isConnected = false;
            throw new LostConnectionException();
        }
    }

    //lancer des exception dans le cas ou serveur ne reagit pas comme prevu

    public void goForward() throws RobotException, IOException {
        out.print("FWD\n");
        out.flush();
        if (!in.readLine().equals("FWD_OK\n")) {
            throw new RobotException();
        }
        isMoving = true;
    }

    public void goBackward() throws IOException, RobotException {
        out.print("BKWD\n");
        out.flush();
        if (!in.readLine().equals("BKWD_OK\n")) {
            throw new RobotException();
        }
        isMoving = true;
    }

    public void goLeft() throws IOException, RobotException {
        out.print("ROTATE_LEFT\n");
        out.flush();
        if (!in.readLine().equals("ROTATE_LEFT_OK\n")) {
            throw new RobotException();
        }
        isMoving = true;
    }

    public void goRight() throws IOException, RobotException {
        out.print("ROTATE_RIGHT\n");
        out.flush();
        if (!in.readLine().equals("ROTATE_RIGHT_OK\n")) {
            throw new RobotException();
        }
        isMoving = true;
    }

    public void stop() throws IOException, RobotException {
        out.print("STOP\n");
        out.flush();
        if (!in.readLine().equals("STOP_OK\n")) {
            throw new RobotException();
        }
        isMoving = false;
    }

    //TODO : a voir avec le protocole pour ces méthodes et la classe interne d'erreur
    public void goFrontLeft() throws IOException, RobotException {
        out.print("FRONT_L\n");
        out.flush();
        if (!in.readLine().equals("FRONT_L_OK\n")) {
            throw new RobotException();
        }
        isMoving = true;
    }

    public void goFrontRight() throws RobotException, IOException {
        out.print("FRONT_R\n");
        out.flush();
        if (!in.readLine().equals("FRONT_R_OK\n")) {
            throw new RobotException();
        }
        isMoving = true;
    }

    public void goBackwardsRight() throws IOException, RobotException {
        out.print("BCK_R\n");
        out.flush();
        if (!in.readLine().equals("BCK_R_OK\n")) {
            throw new RobotException();
        }
        isMoving = true;
    }

    public void goBackwardsLeft() throws IOException, RobotException {
        out.print("BCK_L\n");
        out.flush();
        if (!in.readLine().equals("BCK_L_OK\n")) {
            throw new RobotException();
        }
        isMoving = true;
    }

    public boolean isMoving() {
        return isMoving;
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



}
