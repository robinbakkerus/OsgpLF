#!/bin/bash

java -jar target/core-3.0-allinone.jar 2>&1 > /var/log/osgp/core.log &
echo "see /var/log/osgp/dlms.log" 

