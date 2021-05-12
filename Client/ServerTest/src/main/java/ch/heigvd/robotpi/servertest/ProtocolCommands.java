/*
 * @File ProtocolCommands.java
 * @Authors : David González León
 * @Date 12 mai 2021
 */
package ch.heigvd.robotpi.servertest;

import lombok.Getter;

public enum ProtocolCommands {
   conn("CONN", "CONN_OK"), forward("FWD", "FWD_OK"), backward("BKWD", "BKWD_OK"),
   rotateLeft("ROTATE_LEFT", "ROTATE_LEFT_OK"), rotateRight("ROTATE_RIGHT", "ROTATE_RIGHT_OK"),
   frontleft("FRONT_L", "FRONT_L_OK"), frontRight("FRONT_R", "FRONT_R_OK"), backwardsLeft("BCK_L", "BCK_L_OK"),
   backwardsRight("BCK_R", "BCK_R_OK"), disconnect("DISCONN", "DISCONN_OK"), stop("STOP", "STOP_OK"),
   ping("PING", "PING");

   @Getter private final String message, messageConfirmation;

   ProtocolCommands(String message, String messageConfirmation) {
      this.message = message;
      this.messageConfirmation = messageConfirmation;
   }

   public static ProtocolCommands getCommandFromMessage(String message) {
      if (message.equals(conn.getMessage())) {
         return conn;
      } else if (message.equals(forward.getMessage())) {
         return forward;
      } else if (message.equals(backward.getMessage())) {
         return backward;
      } else if (message.equals(rotateRight.getMessage())) {
         return rotateRight;
      } else if (message.equals(rotateLeft.getMessage())) {
         return rotateLeft;
      } else if (message.equals(frontleft.getMessage())) {
         return frontleft;
      } else if (message.equals(frontRight.getMessage())) {
         return frontRight;
      } else if (message.equals(backwardsRight.getMessage())) {
         return backwardsRight;
      } else if (message.equals(backwardsLeft.getMessage())) {
         return backwardsLeft;
      } else if (message.equals(ping.getMessage())) {
         return ping;
      } else if (message.equals(stop.getMessage())) {
         return stop;
      } else if (message.equals(disconnect.getMessage())) {
         return disconnect;
      }
      return null;
   }
}
