#!/bin/sh

#Usage: ./launch_utt.sh [OPTIONS]
#Options are:
#-h HOST : hostname where rabbitmq lives (default localhost)
java -cp lib/commons-cli-1.1.jar:lib/commons-io-1.2.jar:lib/rabbitmq-client.jar:target/classes com.capgemini.protorabbitmq.utt.Utt 
