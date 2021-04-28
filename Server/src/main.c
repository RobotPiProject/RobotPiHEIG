/*
Taches:
-Réceptionner les commandes du clients(server)
-Gérer la connexion au client(server)
-Piloter les moteurs(motor)
-Gérer une connexion I2C:motor
-Gestion et sauvegarde des paramètres:init
-Interpretation des commandes du protocole:protocol
-Lire le fichier de configuration:init
-Gérer la caméra:camera
*/

#include <stdio.h>
#include "include/init.h"
#include "include/server.h"
#include <sys/socket.h>

int main() {
    server();    
}
