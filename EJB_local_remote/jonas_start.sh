#!/bin/sh

XTRA_CLASSPATH=.:`pwd`/edu/rice/rubis:`pwd`/edu/rice/rubis/servlets
export XTRA_CLASSPATH
JAVA_OPTS="-Xms128m -Xmx768m -Xss16k"
export JAVA_OPTS

#registry &
EJBServer &