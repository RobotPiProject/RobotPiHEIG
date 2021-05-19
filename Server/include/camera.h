/**
 * @file camera.h
 * @author Anthony Jaccard
 * @brief Defines functions to configure the webcam of the robot and take pictures
 * @version 0.1
 * @date 2021-05-10
 * 
 */

#ifndef CAMERA_H
#define CAMERA_H

typedef enum ImageFormat
{
   JPG, PNG
}ImageFormat;

typedef struct CamSettings
{
   unsigned imageWidth;
   unsigned imageHeight;
   ImageFormat format;
   unsigned useBanner;
} CamSettings;

CamSettings getCamSettings();

void setCamSettings(CamSettings newSettings);

/**
 * @brief Takes a picture and stores it in the given folder with the given name
 * 
 * @param name Name of the future picture file, without extension
 * @param path Path to store the image into, must finish with a "/"
 * @return int 0 if no error was encountered, other value otherwise
 */
int camSnap(const char *path, const char *name);

#endif