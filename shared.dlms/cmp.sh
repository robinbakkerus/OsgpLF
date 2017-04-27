#!/bin/bash
# mvn -f pom-pb.xml clean
mkdir target/generated-sources/protobuf/java -p
./protoc -I=../shared/src/main/proto -I=src/main/proto --java_out=target/generated-sources/protobuf/java/ src/main/proto/dlms.proto
cp ../shared/src/main/proto/core.proto  src/main/proto/ -v
mvn -f pom-pb.xml protobuf:compile-custom
mvn -f pom-pb.xml install -DskipTests
rm src/main/proto/core.proto -v


