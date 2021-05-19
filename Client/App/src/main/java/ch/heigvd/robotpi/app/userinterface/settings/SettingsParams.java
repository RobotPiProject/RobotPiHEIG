/*
 * @File SettingsParams.java
 * @Authors : David González León, Jade Gröli
 * @Date 17 avr. 2021
 */
package ch.heigvd.robotpi.app.userinterface.settings;

import lombok.Getter;

/**
 * An enum that lists the different settings present in the settings.properties file.
 */
public enum SettingsParams {
   IP_ADDRESS("ipAddress");

   @Getter private final String paramName;

   /**
    * Instantiates a new Settings params.
    *
    * @param paramName the param name
    */
   SettingsParams(String paramName) {
      this.paramName = paramName;
   }
}
