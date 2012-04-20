package com.capgemini.protorabbitmq.serveurcentral;

import static com.capgemini.protorabbitmq.commun.Commun.TASK_QUEUE_NAME;

import java.io.IOException;

import com.capgemini.protorabbitmq.commun.Helper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class ServeurCentral {

	private int sent_messages = 0;

	public static void main(String[] argv) 
			throws java.io.IOException {

		final ServeurCentral sc = new ServeurCentral();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public synchronized void start() {
				System.out.println(Helper.getFormatedDate() + " [X] Stopping after sending " + sc.getSentMessages() + " messages");
			}
		});
		
		int message_number = 1000;
		if (argv.length >= 1) {
			message_number = Integer.parseInt(argv[0]);
		}
		
		String message = "message from Serveur Central";
		if (argv.length >= 2) {
			message = argv[1];
		}
		
		sc.start(message_number, message);
	}
	
	protected int getSentMessages() {
		return sent_messages;
	}

	public void start(int message_number, String message) throws IOException {


		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
		
		while (sent_messages < message_number) {
			channel.basicPublish( "", TASK_QUEUE_NAME, 
					MessageProperties.PERSISTENT_TEXT_PLAIN,
					message.getBytes());
			
			sent_messages++;
			
			if (sent_messages % 1000 == 0) {
				System.out.println(Helper.getFormatedDate() + " [x] " + sent_messages + " messages sent");
			}
			
		}

		channel.close();
		connection.close();
	}	

}
