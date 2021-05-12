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

void setCamSettings(CamSettings);

/**
 * @brief Takes a picture and stores it in the given folder
 * 
 * @param nameBuf Buffer for the name of the picture
 * @param nameSize Capacity of the buffer for the picture name
 * @param folderName Path of the folder used to store the future picture
 * @param folderNameSize Size of the path name
 * @return unsigned char Number of characters actually used for the name of the picture taken or 0 if there was an error
 */
unsigned char  camSnap(char *nameBuf, const unsigned char nameSize, const char *folderName, const unsigned char  folderNameSize);

#endif