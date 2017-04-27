#!/bin/bash
mvn clean
mvn -f pom-pb.xml protobuf:compile
mvn -f pom-pb.xml protobuf:compile-custom
mvn -f pom-pb.xml install


