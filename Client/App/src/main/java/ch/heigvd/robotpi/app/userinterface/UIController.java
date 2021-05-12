/*
 * @File UIController.java
 * @Authors : David González León
 * @Date 24 mars 2021
 */
package ch.heigvd.robotpi.app.userinterface;

import ch.heigvd.robotpi.app.communication.Client;
import ch.heigvd.robotpi.app.userinterface.settings.SettingsParams;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.concurrent.Semaphore;

/**
 * The controller of the main window of the client's app
 */
public class UIController {
   //Image Width
   private final int IMAGE_SIZE = 80;
   private final Semaphore mutex = new Semaphore(1);
   private final Semaphore mutexPicture = new Semaphore(1);
   //Settings
   private Properties settings;
   private Scene scene;
   private String currentIpAddress;
   private Thread workerThread;
   private Client client;
   private ConnectedWorker worker;
   /**
    * Boolean to know when a key is pressed
    */
   private boolean upPressed = false;
   private boolean rightPressed = false;
   private boolean leftPressed = false;
   private boolean downPressed = false;

   private boolean newInstruction = false;
   private boolean justDisconnected = false;

   @FXML private Button BFrontLeft;
   @FXML private Button BFront;
   @FXML private Button BFrontRight;
   @FXML private Button BLeft;
   @FXML private Button BRight;
   @FXML private Button BBackwardsLeft;
   @FXML private Button BBackwards;
   @FXML private Button BBackwardsRight;
   @FXML private Button BCamera;


   @FXML private Label LConnectionStatus;
   @FXML private TextField TFConnectionAddress;

   @FXML private ImageView imageView;

   /**
    * Sets the scene linked to this controller and sets up all of it's components
    *
    * @param scene the scene
    */
   public void setScene(Scene scene) {
      this.scene = scene;
      //Load settings
      settings = new Properties();
      try {
         settings.load(getClass().getClassLoader().getResourceAsStream("settings.properties"));
      } catch (IOException e) {
         e.printStackTrace();
         Util.createAlertFrame(Alert.AlertType.ERROR, "Error while loading the properties",
                               "Error while loading the properties",
                               "There was an error while loading the properties, the app will close.");
         this.close();
      }

      currentIpAddress = settings.getProperty(SettingsParams.IP_ADDRESS.getParamName());

      //Process settings
      if (!currentIpAddress.equals("")) {
         TFConnectionAddress.setText(currentIpAddress);
      }

      //Setup interaction
      setupKeys();
      setupButtons();

      //Setup skin
      JMetro jMetro = new JMetro(Style.LIGHT);
      jMetro.setScene(scene);

   }

   /**
    * Loads onto the stage the scene, and executes the basic setup for the ui
    *
    * @param primaryStage the primary stage
    */
   public void load(Stage primaryStage) {
      client = new Client();
      worker = new ConnectedWorker();
      workerThread = new Thread(worker);
      workerThread.start();
      primaryStage.setScene(scene);
      primaryStage.showingProperty().addListener(((observableValue, oldValue, showing) -> {
         if (showing) {
            primaryStage.setMinHeight(primaryStage.getHeight());
            primaryStage.setMinWidth(primaryStage.getWidth());
         }
      }));
      primaryStage.setTitle("Robot PI HEIG");
      primaryStage.getIcons().add(new Image("image/logo.png"));
      //handles key pressing
      AnimationTimer timer = new AnimationTimer() {
         @Override
         public void handle(long l) {
            if (worker.isConnected()) {
               justDisconnected = true;
               try {
                  mutex.acquire();
                  if (newInstruction) {
                     if (upPressed) {
                        if (leftPressed) {
                           client.goFrontLeft();
                        } else if (rightPressed) {
                           client.goFrontRight();
                        } else if (!downPressed) {
                           client.goForward();
                        }
                     } else if (downPressed) {
                        if (leftPressed) {
                           client.goBackwardsLeft();
                        } else if (rightPressed) {
                           client.goBackwardsRight();
                        } else {
                           client.goBackward();
                        }
                     } else if (leftPressed) {
                        if (!rightPressed) {
                           client.goLeft();
                        }
                     } else if (rightPressed) {
                        client.goRight();
                     } else {//robot ne bouge pas
                        if (client.isMoving()) { //si le robot n'est pas encore immobilisé
                           client.stop();
                        }
                     }
                     newInstruction = false;
                  }
               } catch (IOException e) {
                  e.printStackTrace();
               } catch (Client.RobotException e) {
                  Util.createAlertFrame(Alert.AlertType.ERROR, "Error while trying to move",
                                        "Error while trying to move",
                                        "The robot seems to have had an error while moving. Please check the robot " +
                                        "and " + "make sure he is not blocked.");
               } catch (InterruptedException e) {
                  e.printStackTrace();
               } finally {
                  mutex.release();
               }


            } else {
               if (justDisconnected) {
                  LConnectionStatus.setText("Disconnected");
                  justDisconnected = false;
               }
            }
         }
      };

      timer.start();
   }

   /**
    * Closes the ui
    */
   public void close() {
      settings.setProperty(SettingsParams.IP_ADDRESS.getParamName(), currentIpAddress);
      if (worker != null) {
         worker.signalShutdown();
         try {
            synchronized (worker) {
               worker.notify();
            }
            workerThread.join();
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
      try {
         mutex.acquire();
         if (client != null && client.isConnected()) {
            client.disconnect();
         }
      } catch (IOException e) {
      } catch (InterruptedException e) {
      } finally {
         mutex.release();
      }

   }

   @FXML
   private void pressOnClose(ActionEvent event) {
      ((Stage) LConnectionStatus.getScene().getWindow()).close();
   }

   @FXML
   private void openAboutPage(ActionEvent event) {
      try {
         if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI("https://github.com/RobotPiProject/RobotPiHEIG"));
         }
      } catch (IOException e) {
         e.printStackTrace();
      } catch (URISyntaxException e) {
         e.printStackTrace();
      }
   }

   @FXML
   private void connectButtonPressed(ActionEvent event) {
      if (TFConnectionAddress.getText().isEmpty()) {
         Util.createAlertFrame(Alert.AlertType.WARNING, "No ip adress", "No ip adress",
                               "Please write the ip adress of the targeted robot before pressing connect.");
         return;
      }
      String ipAdress = TFConnectionAddress.getText();
      if (ipAdress.matches("(?<!\\d|\\d\\.)(?:[01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.(?:[01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                           "(?:[01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.(?:[01]?\\d\\d?|2[0-4]\\d|25[0-5])(?!\\d|\\.\\d)") ||
          ipAdress.equals("localhost") || ipAdress.equals("raspberrypi")) {
         try {
            mutex.acquire();
            client.connect(ipAdress);
            worker.setConnected();
            currentIpAddress = ipAdress;
            synchronized (worker) {
               worker.notify();
            }
         } catch (Client.CantConnectException e) {
            e.printStackTrace();
            Util.createAlertFrame(Alert.AlertType.ERROR, "Error with the robot", "Error with the robot",
                                  "The robot had an issue while connecting to the client. Please restart the robot " +
                                  "then try again");
         } catch (IOException | Client.IncorrectDeviceException e) {
            e.printStackTrace();
            Util.createAlertFrame(Alert.AlertType.ERROR, "Wrong ip adress", "Wrong ip adress",
                                  "The ip adress you wrote does not coincide with that of a robot. Please check the " +
                                  "ip adress of the robot and try again.");
         } catch (InterruptedException e) {
            e.printStackTrace();
         } finally {
            mutex.release();
         }
      } else {
         Util.createAlertFrame(Alert.AlertType.ERROR, "Not an ip adress", "Not an ip adress",
                               "The adress you provided is not a valid ip adress. Please try again.");
      }

   }

   @FXML
   private void openDiscoverWindow(ActionEvent event) {

   }

   @FXML
   private void cameraButtonPressed(ActionEvent event) {
      if (worker.isConnected()) {
         try {
            File figuresDir = new File(new File(".").getCanonicalPath() + "/figures");
            if (!figuresDir.exists()) {
               figuresDir.mkdir();
            }
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
            LocalDateTime now = LocalDateTime.now();
            PictureWorker pictureWorker =
                    new PictureWorker(figuresDir.getPath() + "/" + currentIpAddress + "_" + dtf.format(now));
            Thread pictureThread = new Thread(pictureWorker);
            pictureThread.start();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   private void setupButtons() {
      BBackwards.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
         if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            downPressed = true;
            newInstruction = true;
         }
      });
      BBackwardsLeft.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
         if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            downPressed = true;
            leftPressed = true;
            newInstruction = true;
         }
      });
      BBackwardsRight.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
         if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            downPressed = true;
            rightPressed = true;
            newInstruction = true;
         }
      });
      BFront.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
         if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            upPressed = true;
            newInstruction = true;
         }
      });
      BFrontLeft.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
         if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            upPressed = true;
            leftPressed = true;
            newInstruction = true;
         }
      });
      BFrontRight.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
         if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            upPressed = true;
            rightPressed = true;
            newInstruction = true;
         }
      });
      BLeft.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
         if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            leftPressed = true;
            newInstruction = true;
         }
      });
      BRight.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
         if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            rightPressed = true;
            newInstruction = true;
         }
      });

      //Setup buttons released
      BBackwards.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseEvent -> {
         if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            downPressed = false;
            newInstruction = true;
         }
      });
      BBackwardsLeft.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseEvent -> {
         if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            downPressed = false;
            leftPressed = false;
            newInstruction = true;
         }
      });
      BBackwardsRight.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseEvent -> {
         if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            downPressed = false;
            rightPressed = false;
            newInstruction = true;
         }
      });
      BFront.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseEvent -> {
         if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            upPressed = false;
            newInstruction = true;
         }
      });
      BFrontLeft.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseEvent -> {
         if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            upPressed = false;
            leftPressed = false;
            newInstruction = true;
         }
      });
      BFrontRight.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseEvent -> {
         if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            upPressed = false;
            rightPressed = false;
            newInstruction = true;
         }
      });
      BLeft.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseEvent -> {
         if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            leftPressed = false;
            newInstruction = true;
         }
      });
      BRight.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseEvent -> {
         if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            rightPressed = false;
            newInstruction = true;
         }
      });

      //Setup images Bouttons
      addImageToButton(BRight, "image/RotateRight.png");
      addImageToButton(BLeft, "image/RotateLeft.png");
      addImageToButton(BFrontRight, "image/ForwardTurnRight.png");
      addImageToButton(BFront, "image/Forward.png");
      addImageToButton(BFrontLeft, "image/ForwardTurnLeft.png");
      addImageToButton(BBackwardsRight, "image/BackwardTurnRight.png");
      addImageToButton(BBackwards, "image/Backward.png");
      addImageToButton(BBackwardsLeft, "image/BackwardTurnLeft.png");
      addImageToButton(BCamera, "image/Camera.png");
   }

   private void setupKeys() {
      scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
         switch (keyEvent.getCode()) {
            case LEFT:
               leftPressed = true;
               break;
            case RIGHT:
               rightPressed = true;
               break;
            case DOWN:
               downPressed = true;
               break;
            case UP:
               upPressed = true;
               break;
            default:
               break;
         }
         newInstruction = true;
      });
      scene.addEventFilter(KeyEvent.KEY_RELEASED, keyEvent -> {
         switch (keyEvent.getCode()) {
            case LEFT:
               leftPressed = false;
               break;
            case RIGHT:
               rightPressed = false;
               break;
            case DOWN:
               downPressed = false;
               break;
            case UP:
               upPressed = false;
               break;
            default:
               break;
         }
         newInstruction = true;
      });
      scene.getRoot().requestFocus();
   }

   /**
    * Adds an image to the given button, and sets up the button so it can conveniently show the image
    *
    * @param b        the button to setup
    * @param imageSrc the path to the image
    */
   private void addImageToButton(Button b, String imageSrc) {
      ImageView i = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream(imageSrc)));
      i.setFitWidth(IMAGE_SIZE);
      i.setFitHeight(IMAGE_SIZE);
      b.setMaxHeight(i.getFitHeight());
      b.setMaxWidth(i.getFitWidth());
      b.setGraphic(i);
   }

   /**
    * The worker used to keep the connected RadioButton up to date
    */
   class ConnectedWorker implements Runnable {
      private boolean connected;
      private boolean running = true;

      /**
       * SSignals to the worker that the UI is being closed, and that it needs to stop running
       */
      public void signalShutdown() {
         this.running = false;
         connected = false;
      }

      /**
       * Informs of the status of the connection to the robot
       *
       * @return true if the Client is connected to a robot, false otherwise
       */
      public boolean isConnected() {
         return connected;
      }


      /**
       * Informs the worker that a connection has been made
       */
      public void setConnected() {
         this.connected = true;
         LConnectionStatus.setText("Connected");
      }

      @Override
      public void run() {
         while (running) {
            if (!connected) {
               synchronized (this) {
                  try {
                     wait();
                  } catch (InterruptedException e) {
                     e.printStackTrace();
                  }
               }
            }
            while (connected) {
               try {
                  Thread.sleep(10000);
               } catch (InterruptedException e) {
                  e.printStackTrace();
               }
               try {
                  mutex.acquire();
                  client.ping();
               } catch (InterruptedException e) {
                  e.printStackTrace();
               } catch (Client.LostConnectionException | IOException e) {
                  connected = false;
               } finally {
                  mutex.release();
               }
            }
         }
         System.out.println("Exiting");
      }
   }

   class PictureWorker implements Runnable {
      private final String photoPath;

      PictureWorker(String photoPath) {this.photoPath = photoPath;}
      //TODO handle exceptions
      @Override
      public void run() {
         try {
            mutexPicture.acquire();
            client.takePicture(photoPath);
            Image image = new Image(new BufferedInputStream(new FileInputStream(photoPath)));
            imageView.setImage(image);
         } catch (InterruptedException e) {
            e.printStackTrace();
         } catch (FileNotFoundException e) {
            e.printStackTrace();
         } catch (Client.CantConnectException e) {
            e.printStackTrace();
         } catch (IOException e) {
            e.printStackTrace();
         } catch (Client.RobotException e) {
            e.printStackTrace();
         } finally {
            mutexPicture.release();
         }
      }
   }


}