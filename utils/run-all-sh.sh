#!/bin/bash
set -x

BASEDIR=$(dirname "$0")
cd $BASEDIR

cd ..
cd core
./run-sa.sh

cd ..
cd pf.dlms
./run-sa.sh

cd ..
cd pa.dlms
./run-sa.sh

ps -ef | grep java | grep target | grep allinone

sleep 3

echo "/var/log/osgp/core.log"
cat /var/log/osgp/core.log
echo "/var/log/osgp/platform.log"
cat /var/log/osgp/platform.log
echo "/var/log/osgp/dlms.log"
cat /var/log/osgp/dlms.log



