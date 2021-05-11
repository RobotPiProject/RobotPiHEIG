/*
 * @File SettingsParams.java
 * @Authors : David González León
 * @Date 17 avr. 2021
 */
package ch.heigvd.robotpi.app.userinterface.settings;

import lombok.Getter;

public enum SettingsParams {
   IP_ADDRESS("ipAddress");

   @Getter private final String paramName;

   SettingsParams(String paramName) {
      this.paramName = paramName;
   }
}
