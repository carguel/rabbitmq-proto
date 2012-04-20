package com.capgemini.protorabbitmq.utt;

import static com.capgemini.protorabbitmq.commun.Commun.TASK_QUEUE_NAME;

import java.io.IOException;

import com.capgemini.protorabbitmq.commun.Helper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class Utt {

	private int received_messages = 0;
	private Thread startThread;
	
	public static void main(String[] argv)
			throws java.io.IOException,
			java.lang.InterruptedException {


		final Utt utt = new Utt();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public synchronized void start() {
				utt.stop();
				
				try {
					Thread.sleep(1000l);
				} catch (InterruptedException e) {					
					e.printStackTrace();
				}
				
				
				System.out.println("\n [*] Stopping after receiving " + utt.getReceivedMessages() + " messages");
			}
		});
		
		utt.start();
	}
	
	protected int getReceivedMessages() {
		return received_messages;
	}
	
	public void start() throws IOException, ShutdownSignalException, ConsumerCancelledException {
		
		startThread = Thread.currentThread();
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
		System.out.println(Helper.getFormatedDate() + " [*] Waiting for messages. To exit press CTRL+C");

		channel.basicQos(1);
		DefaultConsumer c;
		QueueingConsumer consumer = new QueueingConsumer(channel);
		boolean autoAck = false;
		channel.basicConsume(TASK_QUEUE_NAME, autoAck, consumer);		
		
		
		while (! Thread.interrupted()) {
			try {
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			String message = new String(delivery.getBody());
			received_messages++;

			if (received_messages % 1000 == 0) {
				System.out.println(Helper.getFormatedDate() + " [x] " + received_messages + " messages received");   
			}

			
			channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
			} 
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		

	}
	
	public void stop() {
		startThread.interrupt();
	}
}