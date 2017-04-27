#!/bin/bash
echo "you may use args like '-Dstub=true -Dwait=3000'"
java -jar $1 target/protocol-adapter-dlms-3.0-allinone.jar 2>&1 > /var/log/osgp/dlms.log &
echo "see /var/log/osgp/dlms.log" 

