#!/bin/sh

# This requires docker to be installed See https://docs.docker.com/install/

echo "Checking that dockerd is installed..."
if command -v dockerd ; then
	echo "dockerd is installed"
else
	echo "Please install docker If you are using ubuntu, you can try ./ubuntu-install-docker.sh Else see https://docs.docker.com/install/"
	exit 1
fi
echo "Checking if dockerd is already running..."
if docker ps ; then
	echo "dockerd is already started...no need to start again."
else
	echo "Starting docker daemon..."
	sudo dockerd
fi
echo "Starting jboss/keycloak..."
DIR="$( cd "$(dirname "$0")" ; pwd -P )"
docker run -p 9999:8080 -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=password -e KEYCLOAK_IMPORT=/tmp/demo-realm.json -v "$DIR/demo-realm.json:/tmp/demo-realm.json" jboss/keycloak:7.0.0
