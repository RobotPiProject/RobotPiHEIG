#!/bin/bash
#
#	setup_app.sh	-	sets up the client app
#
#	usage:	setup_app
#
#  David González León	2021-06-04

mvn clean package
cp App/target/App-1.0-Final.jar App-1.0-Final.jar