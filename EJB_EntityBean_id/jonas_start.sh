#!/bin/sh

XTRA_CLASSPATH=.:`pwd`/edu/rice/rubis:`pwd`/edu/rice/rubis/servlets
export XTRA_CLASSPATH

#registry &
EJBServer &