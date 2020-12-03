#! /bin/bash
export PGPORT=9998
export PGDATA=/tmp/$USER/myDB/data
echo "creating db named ... "$USER"_DB"
createdb -h localhost -p $PGPORT $USER"_DB"
/usr/lib/postgresql/12/bin/pg_ctl status

echo "Copying csv files ... "
sleep 1
cp ../data/*.csv /tmp/$USER/myDB/data/.

echo "Initializing tables .. "
sleep 1
psql -h localhost -p $PGPORT $USER"_DB" < ../sql/create.sql
