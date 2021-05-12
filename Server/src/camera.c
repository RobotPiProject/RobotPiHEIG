#include <camera.h>

CamSettings settings = {512, 512, JPG, 0};

CamSettings getCamSettings()
{
   return settings;
}

void setCamSettings(CamSettings newSettings)
{
   settings = newSettings;
}

unsigned char  camSnap(char *nameBuf, const unsigned char nameSize, const char *folderName, const unsigned char  folderNameSize)
{
   char command[128];
   return 0;
}