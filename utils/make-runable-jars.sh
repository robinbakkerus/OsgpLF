#!/bin/bash

#ant -f MakeOsgpCore.xml
#ant -f MakeOsgpPlatform.xml
#ant -f MakePaOsgpDlms.xml

function doit {
  cd /tmp  
  mkdir $1
  cd $1
  rm -rf *
  unzip -q ../$2 
  rm reference.conf
  zip -q ../$1.jar . -r . 
}

doit osgp-core 		OsgpCore.jar
#doit osgp-platform 	OsgpPlatform.jar
#doit osgp-dlms 		OsgpPaDlms.jar 


   


