#!/bin/bash

cd /home/dyna

#refreshpostgres.bat
killall -9 proxy
pg_ctl stop -m immediate
killall -9 postmaster
/raid/dyna/pg8/bin/postmaster -c config_file=/raid/dropsho/Rubis/scripts/postgresql.conf &
sleep 4
#"psql tpcw < /raid/dyna/etc/triggers.sql" &> /dev/null
#psql tpcw < /raid/dyna/etc/triggers.sql.tpcw
#psql tpcb < /raid/dyna/etc/triggers.sql.tpcb
#psql tpcc < /raid/dyna/etc/triggers.sql.tpcc
pg_ctl stop -m fast;
#sleep 1
#sleep 2
ipcrm -M 9
ipcrm -S 8

echo "Starting postmaster piped to &> /dev/null: NO PRINTOUTS!"
/raid/dyna/pg8/bin/postmaster -c config_file=/raid/dropsho/Rubis/scripts/postgresql.conf &> /dev/null

# now type psql to get into the sql environment


