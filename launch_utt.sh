#!/bin/sh

#Usage: ./launch_utt.sh
java -cp lib/commons-cli-1.1.jar:lib/commons-io-1.2.jar:lib/rabbitmq-client.jar:target/classes com.capgemini.protorabbitmq.utt.Utt 
