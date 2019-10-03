#!/bin/sh --

export CONTAINER=$(docker ps -lq)
docker exec -it $CONTAINER keycloak/bin/standalone.sh \
  -Djboss.socket.binding.port-offset=100 -Dkeycloak.migration.action=export \
  -Dkeycloak.migration.provider=singleFile \
  -Dkeycloak.migration.realmName=demo \
  -Dkeycloak.migration.usersExportStrategy=REALM_FILE \
  -Dkeycloak.migration.file=/tmp/demo-realm.json
