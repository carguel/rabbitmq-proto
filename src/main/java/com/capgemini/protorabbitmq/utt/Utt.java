package com.capgemini.protorabbitmq.utt;

import static com.capgemini.protorabbitmq.commun.Commun.TASK_QUEUE_NAME;

import java.io.IOException;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

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
	private Date firstMessageDate = null;
	private int accumulatedSize = 0;
	private Date lastMessageDate = null;
	
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
				
				double receivingTime = utt.getReceivingTime();
				int receivedMessage = utt.getReceivedMessages();
				double receivedMegaByte = utt.getMegaByteReceived();
				double messagePerSecond = utt.getMessagePerSecond();
				double megaBytePersecond = utt.getMegaBytePerSecond();
				String message = "\n [*] Stopping :  %d messages /  %.3f MB received in %.3f seconds ( %.3f msg/s / %.3f MB/s)";				
				Formatter formatter = new Formatter(System.out);
				formatter.format(Locale.ENGLISH, message, receivedMessage, receivedMegaByte, receivingTime, messagePerSecond, megaBytePersecond);
				formatter.flush();
			}
		});
		
		String rabbitmqHost = "localhost";
		
		for (int i= 0; i < argv.length; i++) {
			if (argv[i].equals("-h")) {
				rabbitmqHost = argv[i + 1];
			}
			
		}
		
		utt.start(rabbitmqHost);
	}
	
	protected double getMegaBytePerSecond() {
		return  getMegaByteReceived() / getReceivingTime();
	}

	protected double getMessagePerSecond() {
		return (double)(getReceivedMessages() / getReceivingTime());
	}

	protected int getReceivedMessages() {
		return received_messages;
	}
	
	protected double getReceivingTime() {
		return (lastMessageDate.getTime() - firstMessageDate.getTime()) / 1000.0;
	}
	
	protected double getMegaByteReceived() {
		return accumulatedSize / 1000.0 / 1000.0;
	}
	
	public void start(String rabbitmqHost) throws IOException, ShutdownSignalException, ConsumerCancelledException {
		
		startThread = Thread.currentThread();
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(rabbitmqHost);
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
		System.out.println(Helper.getFormatedDate() + " [*] Waiting for messages. To exit press CTRL+C");

		channel.basicQos(1);
		QueueingConsumer consumer = new QueueingConsumer(channel);
		boolean autoAck = false;
		channel.basicConsume(TASK_QUEUE_NAME, autoAck, consumer);		
		
		
		while (! Thread.interrupted()) {
			try {
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			String message = new String(delivery.getBody());
			
			if (firstMessageDate == null) {
				firstMessageDate = new Date();
			}
			
			received_messages++;
			accumulatedSize += message.length();
			lastMessageDate = new Date();

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