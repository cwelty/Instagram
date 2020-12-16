#! /bin/bash
export PGPORT=9998                                                                                                                                                                                                                           export PGDATA=/tmp/$USER/myDB/data
/usr/lib/postgresql/13/bin/pg_ctl -o "-c unix_socket_directories=$PGSOCKETS -p $PGPORT" -D $PGDATA -l $folder/logfile stop
