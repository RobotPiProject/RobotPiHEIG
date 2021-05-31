#include <camera.h>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>

CamSettings settings = {512, 512, JPG, 0};

CamSettings getCamSettings()
{
   return settings;
}

void setCamSettings(CamSettings newSettings)
{
   settings = newSettings;
}

int camSnap(const char *path, const char *name)
{
   char command[256] = "fswebcam";

   //Set resolution
   char tmp[128];
   sprintf(tmp, " -r %ux%u", settings.imageWidth, settings.imageHeight);
   strcat(command, tmp);

   //Set banner
   if(!settings.useBanner)
   {
      strcat(command, " --no-banner");
   }

   //Set file path
   sprintf(tmp, " %s/%s", path, name);
   strcat(command, tmp);

   //Set file format
   char format[4];
   switch (settings.format)
   {
   case JPG:
      sprintf(format, "jpg");
      break;
   case PNG:
      sprintf(format, "png");
      break;
   default:
      break;
   }
   sprintf(tmp, ".%s", format);
   strcat(command, tmp);
   printf("Trying to take picture with command: %s\n", command);

   return system(command);
}