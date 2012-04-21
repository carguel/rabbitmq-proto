#!/bin/sh

#Usage : ./launch_sc.sh [OPTIONS]
# Options are:
# -n MESSAGE NUMBER : number of messages to send (default 1000)
# -s MESSAGE_SIZE : size of message to send (default 100 bytes)
# -h HOST : hostaname where rabbitmq lives (default localhost)
java -cp lib/commons-cli-1.1.jar:lib/commons-io-1.2.jar:lib/rabbitmq-client.jar:target/classes com.capgemini.protorabbitmq.serveurcentral.ServeurCentral "$@"
