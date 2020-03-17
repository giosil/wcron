FROM jboss/wildfly
ENV DEPLOY_DIR /opt/jboss/wildfly/standalone/deployments
COPY target/wcron.war $DEPLOY_DIR
