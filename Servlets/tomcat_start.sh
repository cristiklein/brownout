#!/bin/sh

CLASSPATH=${CLASSPATH}:.:/users/margueri/RUBiS/Servlets/rubis_servlets.jar:/usr/local/java/mysql/mm.mysql-2.0.4-bin.jar
export CLASSPATH

/opt/jakarta-tomcat-3.2.3/bin/startup.sh
