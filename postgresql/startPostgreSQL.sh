#! /bin/bash
folder=/tmp/$USER
export PGDATA=$folder/myDB/data
export PGSOCKETS=$folder/myDB/sockets

echo $folder

#Clear folder
rm -rf $folder/myDB

#Initialize folders
mkdir $folder
mkdir $folder/myDB
mkdir $folder/myDB/data
mkdir $folder/myDB/sockets
sleep 1
#cp ../data/*.csv $folder/myDB/data

#Initialize DB
/usr/lib/postgresql/12/bin/initdb

sleep 1
#Start folder
export PGPORT=9998
/usr/lib/postgresql/12/bin/pg_ctl -o "-c unix_socket_directories=$PGSOCKETS -p $PGPORT" -D $PGDATA -l $folder/logfile start

