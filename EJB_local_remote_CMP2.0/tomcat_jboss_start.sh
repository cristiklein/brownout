#!/bin/sh

CLASSPATH=${CLASSPATH}:`pwd`/rubis_ejb_servlets.jar:${JBOSS_DIST}/lib/ext/jboss.jar:${JBOSS_DIST}/lib/ext/jnpserver.jar:${JBOSS_DIST}/lib/ext/jndi.jar:${JBOSS_DIST}/client/jboss-client.jar:${JBOSS_DIST}/client/jnp-client.jar:
export CLASSPATH

### JBoss + JNP ###
export TOMCAT_OPTS="-Xmx512m -Xss16k -Djava.naming.factory.initial=org.jnp.interfaces.NamingContextFactory -Djava.naming.provider.url=localhost -Djava.naming.factory.url.pkgs=org.jboss.naming:org.jnp.interfaces"

/opt/jakarta-tomcat-3.2.3/bin/startup.sh
