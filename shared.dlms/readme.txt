
om proto file te compileren:

mvn protobuf:compile
mvn protobuf:compile-custom

protoc -I=/tmp -I=src/main/proto --java_out=target/generated-sources/protobuf/java/ src/main/proto/dlms.proto


protoc -I=/tmp -I=src/main/proto --plugin=protoc-gen-grpc-java=/my-dev/protoc-gen-grpc-java --grpc -java_out=/tmp
