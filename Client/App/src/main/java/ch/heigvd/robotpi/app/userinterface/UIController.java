/*
 * @File UIController.java
 * @Authors : David González León
 * @Date 24 mars 2021
 */
package ch.heigvd.robotpi.app.userinterface;

import ch.heigvd.robotpi.app.communication.Client;
import ch.heigvd.robotpi.app.userinterface.settings.SettingsParams;
import ch.heigvd.robotpi.servertest.ProtocolCommands;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.concurrent.Semaphore;

/**
 * The controller of the main window of the client's app. It is linked to the mainView.fxml file.
 */
public class UIController {
   //Image Width
   private final int IMAGE_SIZE = 80;
   private final Semaphore mutex = new Semaphore(1);
   private final Semaphore mutexPicture = new Semaphore(1);
   //Settings
   private Properties settings;
   private String currentIpAddress;
   //Scene
   private Scene scene;
   //Threading and client
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
   private ProtocolCommands lastCommand = null;

   //FXML instances
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
      //Create client and threading
      client = new Client();
      worker = new ConnectedWorker();
      workerThread = new Thread(worker);
      workerThread.start();

      //Set scene and add set settings/logo...
      primaryStage.setScene(scene);
      primaryStage.showingProperty().addListener(((observableValue, oldValue, showing) -> {
         if (showing) {
            primaryStage.setMinHeight(primaryStage.getHeight());
            primaryStage.setMinWidth(primaryStage.getWidth());
         }
      }));
      primaryStage.setTitle("Robot PI HEIG");
      primaryStage.getIcons().add(new Image("image/logo.png"));

      //handles key/button pressing
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
                           if (lastCommand != ProtocolCommands.frontleft) {
                              client.goFrontLeft();
                              lastCommand = ProtocolCommands.frontleft;
                           }
                        } else if (rightPressed) {
                           if (lastCommand != ProtocolCommands.frontRight) {
                              client.goFrontRight();
                              lastCommand = ProtocolCommands.frontRight;
                           }
                        } else if (!downPressed) {
                           if (lastCommand != ProtocolCommands.forward) {
                              client.goForward();
                              lastCommand = ProtocolCommands.forward;
                           }
                        }
                     } else if (downPressed) {
                        if (leftPressed) {
                           if (lastCommand != ProtocolCommands.backwardsLeft) {
                              client.goBackwardsLeft();
                              lastCommand = ProtocolCommands.backwardsLeft;
                           }
                        } else if (rightPressed) {
                           if (lastCommand != ProtocolCommands.backwardsRight) {
                              client.goBackwardsRight();
                              lastCommand = ProtocolCommands.backwardsRight;
                           }
                        } else {
                           if (lastCommand != ProtocolCommands.backward) {
                              client.goBackward();
                              lastCommand = ProtocolCommands.backward;
                           }
                        }
                     } else if (leftPressed) {
                        if (!rightPressed) {
                           if (lastCommand != ProtocolCommands.rotateLeft) {
                              client.goLeft();
                              lastCommand = ProtocolCommands.rotateLeft;
                           }
                        }
                     } else if (rightPressed) {
                        if (lastCommand != ProtocolCommands.rotateRight) {
                           client.goRight();
                           lastCommand = ProtocolCommands.rotateRight;
                        }
                     } else {//robot ne bouge pas
                        if (client.isMoving()) { //si le robot n'est pas encore immobilisé
                           client.stop();
                           lastCommand = ProtocolCommands.stop;
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
               } catch (Client.CantConnectException e) {

               } finally {
                  mutex.release();
               }
            } else {
               if (justDisconnected) {
                  worker.setDisconnected();
                  justDisconnected = false;
               }
            }
         }
      };

      timer.start();
   }

   /**
    * Closes the ui and all the active threads
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

   /**
    * Action when the user selects the closing option in the menu. Closes the ui
    *
    * @param event the event
    */
   @FXML
   private void pressOnClose(ActionEvent event) {
      ((Stage) LConnectionStatus.getScene().getWindow()).close();
   }

   /**
    * Action when the user selects the about option in the menu. Opens the about page of this project, in our case
    * the github page.
    *
    * @param event the event
    */
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

   /**
    * Action when the user presses the connect button on the ui or the option menu. Starts the connection procedure.
    * Can create different error messages if the user input is incorrect, or if there are no devices that correspond
    * to the given input
    *
    * @param event the event
    */
   @FXML
   private void connectButtonPressed(ActionEvent event) {
      if (worker.isConnected()) {
         Util.createAlertFrame(Alert.AlertType.WARNING, "Already Connected", "Already Connected",
                               "You are already connected to a robot. Please disconnect before attempting to " +
                               "reconnect.");
         return;
      }
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
            Util.createAlertFrame(Alert.AlertType.ERROR, "Error with the robot", "Error with the robot",
                                  "The robot had an issue while connecting to the client. Please restart the robot " +
                                  "then try again");
            worker.setDisconnected();
         } catch (IOException | Client.IncorrectDeviceException e) {
            Util.createAlertFrame(Alert.AlertType.ERROR, "Wrong ip adress", "Wrong ip adress",
                                  "The ip adress you wrote does not coincide with that of a robot. Please check the " +
                                  "ip adress of the robot and try again.");
         } catch (InterruptedException e) {
         } finally {
            mutex.release();
         }
      } else {
         Util.createAlertFrame(Alert.AlertType.ERROR, "Not an ip adress", "Not an ip adress",
                               "The adress you provided is not a valid ip adress. Please try again.");
      }

   }

   /**
    * Action when the user selects the disconnect option in the menu. Disconnects the client from the robot, unless
    * there was no connection.
    *
    * @param event the event
    */
   @FXML
   private void disconnectButtonPressed(ActionEvent event) {
      if (!worker.isConnected()) {
         Util.createAlertFrame(Alert.AlertType.WARNING, "You are not connected", "You are not connected",
                               "You are not connected to a robot. Please connect to a device before attempting to " +
                               "disconnect again.");
         return;
      }
      try {
         mutex.acquire();
         client.disconnect();
      } catch (IOException e) {
         e.printStackTrace();
      } catch (InterruptedException e) {
         e.printStackTrace();
      } finally {
         mutex.release();
      }
      worker.setDisconnected();

   }

   /**
    * Action when the user presses the connect button on the ui or the option menu. Launches a new window controlled
    * by the DiscoveryController class. The current window will wait for the new window to close before allowing
    * further interactions
    *
    * @param event the event
    */
   @FXML
   private void openDiscoverWindow(ActionEvent event) {
      try {
         FXMLLoader discoveryViewLoader = new FXMLLoader();
         discoveryViewLoader.setLocation(getClass().getClassLoader().getResource("discoveryView.fxml"));
         Scene discoveryScene = new Scene(discoveryViewLoader.load());
         DiscoveryController discoveryController = discoveryViewLoader.getController();
         discoveryController.setScene(discoveryScene, client, TFConnectionAddress);
         Stage stage = new Stage();
         stage.setAlwaysOnTop(true);
         discoveryController.load(stage);
         stage.initOwner(scene.getWindow());
         stage.initModality(Modality.APPLICATION_MODAL);
         stage.showAndWait();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   /**
    * Action when the user presses the camera button on the ui. If it is connected to a robopi device, it will ask
    * for a photo to be taken by the device, and store the received image next to the .jar file.
    *
    * @param event the event
    */
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

   /**
    * Sets up the different buttons to enable the control of the robot through the UI
    */
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

   /**
    * Sets up a reaction for specific keys to enable the control of the robot through them
    */
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
               return;
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
               return;
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
       * Signals to the worker that the UI is being closed, and that it needs to stop running
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

      /**
       * Informs the worker that will he was waiting the connection was lost
       */
      public void setDisconnected() {
         this.connected = false;
         LConnectionStatus.setText("Disconnected");
      }

      @Override
      public void run() {
         while (running) {
            //Wait until a connection is established
            if (!connected) {
               synchronized (this) {
                  try {
                     wait();
                  } catch (InterruptedException e) {
                     e.printStackTrace();
                  }
               }
            }
            //While connected, send ping every 10 sec to ensure the connection is still alive
            while (connected) {
               try {
                  Thread.sleep(10000);
               } catch (InterruptedException e) {
                  e.printStackTrace();
               }
               if (!connected) {
                  break;
               }
               try {
                  mutex.acquire();
                  client.ping();
               } catch (InterruptedException e) {
                  e.printStackTrace();
               } catch (Client.LostConnectionException | IOException e) {
                  setDisconnected();
               } finally {
                  mutex.release();
               }
            }
         }
         System.out.println("Exiting");
      }
   }

   /**
    * A worker that handles the picture process
    */
   class PictureWorker implements Runnable {
      private final String photoPath;

      /**
       * Instantiates a new Picture worker.
       *
       * @param photoPath the photo path
       */
      PictureWorker(String photoPath) {this.photoPath = photoPath;}

      @Override
      public void run() {
         BufferedInputStream stream = null;
         try {
            mutexPicture.acquire();
            client.takePicture(photoPath);
            stream = new BufferedInputStream(new FileInputStream(photoPath));
            Image image = new Image(stream);
            imageView.setImage(image);
            imageView.setFitWidth(image.getWidth());
            imageView.setFitHeight(image.getHeight());
         } catch (InterruptedException e) {
            e.printStackTrace();
         } catch (IOException e) {
            e.printStackTrace();
         } catch (Client.CantConnectException e) {
            Util.createAlertFrame(Alert.AlertType.ERROR, "Connection lost", "Connection lost",
                                  "The robot had an issue while connecting to the client. Please restart the robot " +
                                  "then try again");
            worker.setDisconnected();
         } catch (Client.RobotException e) {
            Util.createAlertFrame(Alert.AlertType.ERROR, "The robot had an error while taking the picture",
                                  "The robot had an error while taking the picture",
                                  "There was an issue with the robot while taking a picture. Please check that the " +
                                  "robot is fine then try again.");
         } finally {
            mutexPicture.release();
            if (stream!=null){
               try {
                  stream.close();
               } catch (IOException e) {
               }
            }
         }
      }
   }
}
