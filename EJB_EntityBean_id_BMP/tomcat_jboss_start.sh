#!/bin/bash

CLASSPATH=${CLASSPATH}:/users/margueri/RUBiS/EJB_EntityBean_id_BMP/rubis.jar:/users/margueri/RUBiS/EJB_EntityBean_id_BMP/rubis_ejb_servlets.jar:${JBOSS_DIST}/client/jnet.jar:${JBOSS_DIST}/client/jboss-j2ee.jar:${JBOSS_DIST}/client/jboss-client.jar:${JBOSS_DIST}/client/jbosssx-client.jar:${JBOSS_DIST}/client/jnp-client.jar:${JBOSS_DIST}/client/jboss-common-client.jar:${JBOSS_DIST}/client/log4j.jar:${JBOSS_DIST}/client/jbossmq-client.jar:${JBOSS_DIST}/client/concurrent.jar

export CLASSPATH

### JBoss + JNP ###
export TOMCAT_OPTS="-Xmx512m -Xss96k -Djava.naming.factory.initial=org.jnp.interfaces.NamingContextFactory -Djava.naming.provider.url=sci20 -Djava.naming.factory.url.pkgs=org.jboss.naming:org.jnp.interfaces"

/opt/jakarta-tomcat-3.2.3/bin/startup.sh
