#!/bin/bash

PID_CORE=$(ps aux | grep 'java' | grep 'core-2.0-allinone' | awk '{print $2}')
PID_PF=$(ps aux | grep 'java' | grep 'platform-dlms-2.0-allinone' | awk '{print $2}')
PID_DLMS=$(ps aux | grep 'java' | grep 'protocol-adapter-dlms-2.0-allinone' | awk '{print $2}')

if [ $PID_CORE ];then kill $PID_CORE; fi
if [ $PID_PF ];then kill $PID_PF; fi
if [ $PID_DLMS ];then kill $PID_DLMS; fi

ps -ef|grep java|grep allinone

