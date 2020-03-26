#!/bin/bash

echo "=> Executing setup script"

JBOSS_HOME=/opt/jboss/wildfly
JBOSS_CLI=$JBOSS_HOME/bin/jboss-cli.sh
SETUP_DIR=$JBOSS_HOME/setup

function wait_for_server() {
  until `$JBOSS_CLI -c ":read-attribute(name=server-state)" 2> /dev/null | grep -q running`; do
    sleep 1
  done
}

# Starting WildFly server in background
echo "=> Starting WildFly server"
echo "JBOSS_HOME: " $JBOSS_HOME
echo "JBOSS_CLI : " $JBOSS_CLI
echo "SETUP_DIR : " $SETUP_DIR

$JBOSS_HOME/bin/standalone.sh -b 0.0.0.0 &

echo "=> Waiting for the server to boot"
wait_for_server

# Add hsqldb module
echo "=> Add hsqldb module"
$JBOSS_CLI -c --command="module add --name=org.hsqldb --resources=$SETUP_DIR/hsql.jar --dependencies=javax.api,javax.transaction.api"

# Add hsqldb driver (driver-class-name instead of driver-xa-datasource-class-name)
echo "=> Add hsqldb driver"
$JBOSS_CLI -c --command="/subsystem=datasources/jdbc-driver=hsqldb:add(driver-name=hsqldb,driver-module-name=org.hsqldb,driver-class-name=org.hsqldb.jdbcDriver)"

# Add datasource
echo "=> Add datasource"
$JBOSS_CLI -c --command="/subsystem=datasources/data-source=db_wcron:add(jndi-name=java:/jdbc/db_wcron, driver-name=hsqldb, connection-url=jdbc:hsqldb:mem:wcron)"

# Deploy application 
echo "=> Deploy application"
$JBOSS_CLI -c --command="deploy $SETUP_DIR/wcron.war --force"

# Shutting down 
echo "=> Shutting down WildFly"
$JBOSS_CLI -c --command="shutdown"

# Restarting WildFly server in foreground
echo "=> Restarting WildFly"
$JBOSS_HOME/bin/standalone.sh -b 0.0.0.0
