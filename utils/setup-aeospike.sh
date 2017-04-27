#!/bin/bash

AES_CONF="aerospike.conf"
DBS_HOME="/my-dev/dbs/aerospike"
AES_HOME="/my-dev/dbs/aerospike-data"

echo "aerospike wordt geinstalleerd in $DBS_HOME en $AES_HOME"

if [ ! -f $AES_CONF ]; then
	echo "Dit script moet gestart worden in scripts folder"
fi

cp $AES_CONF /tmp 

if [ ! -d $DBS_HOME ];then
	mkdir $DBS_HOME -p -v
fi
cd $DBS_HOME

wget -O aerospike.tgz 'http://aerospike.com/download/server/latest/artifact/tgz'
tar -xvf aerospike.tgz
cd aerospike-server

sudo bin/aerospike init --home $AES_HOME
cd $AES_HOME
sudo cp /tmp/$AES_CONF ./etc -v

echo ""
echo "om te starten/stoppen kun je resp start-aerscript resp stop aeroscript gebruiken"
echo ""

