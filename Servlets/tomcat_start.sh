#!/bin/sh

CLASSPATH=${CLASSPATH}:.:`pwd`/edu/rice/rubis/servlets:
export CLASSPATH

/opt/jakarta-tomcat-3.2.3/bin/startup.sh
