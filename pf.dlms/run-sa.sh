#!/bin/bash

java -jar target/platform-dlms-3.0-allinone.jar 2>&1 > /var/log/osgp/platform.log &
echo "see /var/log/osgp/platform.log" 

