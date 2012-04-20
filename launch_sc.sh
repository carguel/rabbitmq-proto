#!/bin/sh

#Usage : ./launch_sc.sh [MESSAGE NUMBER] [MESSAGE TEXT]
# MESSAGE NUMBER : number of messages to send (default 1000)
# MESSAGE TEXT : text of the message (default "message from SC")
java -cp lib/commons-cli-1.1.jar:lib/commons-io-1.2.jar:lib/rabbitmq-client.jar:target/classes com.capgemini.protorabbitmq.serveurcentral.ServeurCentral "$@"
