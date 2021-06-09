# Projet

## Installation

Ce projet nécessite java 11 et maven pour pouvoir être lancé/compilé.

## Lancement de l'application

Si vous voulez lancer l'application client, il suffit d'exécuter le main de la classe FakeMain, ou après un `mvn clean package` de lancer le jar "ClientAppRobotPi-1.0-Final.jar" généré dans le dossier target. Le script setup_app.sh permet de faire les étapes mentionnées ci-dessus directement.

Pour générer la javadoc du projet, il faut lancer la commande `mvn clean package javadoc:javadoc`. Le site sera généré dans le dossier target/site/apidocs.