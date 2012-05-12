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
		
		int messageNumber = 1000;
		int messageSize = 100;
		String rabbitmqHost = "localhost";
		
		for (int i= 0; i < argv.length; i++) {
			if (argv[i].equals("-n")) {
				messageNumber = Integer.parseInt(argv[i+1]);
				i++;
			}
			else if (argv[i].equals("-s")) {
				messageSize = Integer.parseInt(argv[i + 1]);
			}
			else if (argv[i].equals("-h")) {
				rabbitmqHost = argv[i + 1];
			}
			
		}
		
		String message = Helper.generateMessage(messageSize);
		
		sc.start(messageNumber, message, rabbitmqHost);
	}
	
	protected int getSentMessages() {
		return sent_messages;
	}

	public void start(int messageNumber, String message, String rabbitmqHost) throws IOException {


		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(rabbitmqHost);
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
		
		while (sent_messages < messageNumber) {
			channel.basicPublish( "", TASK_QUEUE_NAME, 
					MessageProperties.TEXT_PLAIN,
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
