#!/bin/sh

# Copyright Jan 15, 2007: Steven.Dropsho@EPFL.ch

################################################
# 
# SET THIS VALUE!
#
# Set value to install path of RUBiS
RUBIS_DIR="/raid/dropsho/Rubis/RUBiS"
#
################################################

if [ -z $1 ]; 
then
    echo "Usage error: Must enter the number of users/items/old_items (in thousands, 10=>10,000)"
    exit
fi

# Create a subdirectory to move the results so multiple runs do not clutter up
mkdir -p $1

echo "CREATEDB: Create empty RUBiS DB. createdb called..."
dropdb rubis | grep -i error
createdb rubis | grep -i error

echo "PSQL: creating empty tables in rubis...."
psql -d rubis -f ./input_to_psql_to_create_tables.sql | grep -i error

echo "PERL: create SQL insert commands for fast generation of rubis database"
./fast_fill_db.pl $RUBIS_DIR rubis.properties_template $1

# Only process indices after DB created. Much faster.
echo "PSQL: creating indices in rubis...."
psql -d rubis -f ./input_to_psql_to_create_indices.sql | grep -i error

# Compact DB
psql -d rubis VACUUM FULL

# Move rubis properties file that is generated (with correct user/item counts)
mv rubis.properties.$1 $1

# tar up the database if want to archive it
echo "MANUALLY -> Now tar up of $PGDATA and copy it ..."
#tar -cf /home/dyna/dbimage/rubis.database.$1.tar $PGDATA
du -s $PGDATA
