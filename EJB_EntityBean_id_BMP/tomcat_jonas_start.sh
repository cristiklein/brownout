#!/bin/sh

. $JONAS_ROOT/bin/unix/config_env

# classpath for jonas2.6 rmi
#CLASSPATH=${CLASSPATH}:.:/users/margueri/RUBiS/EJB_EntityBean_id_BMP/rubis.jar:/users/margueri/RUBiS/EJB_EntityBean_id_BMP/rubis_ejb_servlets.jar:${J2EE_HOME}/lib/j2ee.jar:${JONAS_ROOT}/lib/RMI_client.jar:${JONAS_ROOT}/lib/RMI_jonas.jar

# classpath for jonas2.6 jeremie
CLASSPATH=${CLASSPATH}:.:/users/margueri/RUBiS/EJB_EntityBean_id_BMP/rubis.jar:/users/margueri/RUBiS/EJB_EntityBean_id_BMP/rubis_ejb_servlets.jar:${J2EE_HOME}/lib/j2ee.jar:${JONAS_ROOT}/lib/JEREMIE_client.jar:${JONAS_ROOT}/lib/JEREMIE_jonas.jar:${JONAS_ROOT}/config

# classpath for jonas2.5
CLASSPATH=${CLASSPATH}:.:/users/margueri/RUBiS/EJB_EntityBean_id_BMP/rubis.jar:/users/margueri/RUBiS/EJB_EntityBean_id_BMP/rubis_ejb_servlets.jar:${J2EE_HOME}/lib/j2ee.jar

export CLASSPATH

### Jonas + rmi ###
export TOMCAT_OPTS="-Xmx512m -Xss96k -Djava.naming.factory.initial=com.sun.jndi.rmi.registry.RegistryContextFactory -Djava.naming.provider.url=rmi://sci20:1099 -Djava.naming.factory.url.pkgs=org.objectweb.jonas.naming"



### Jonas + jeremie ###
#export TOMCAT_OPTS="-Xmx512m -Xss96k -Djava.naming.factory.initial=org.objectweb.jeremie.libs.services.registry.jndi.JRMIInitialContextFactory -Djava.naming.provider.url=jrmi://sci20:12340 -Djava.naming.factory.url.pkgs=org.objectweb.jonas.naming"

/opt/jakarta-tomcat-3.2.3/bin/startup.sh
