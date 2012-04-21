package com.capgemini.protorabbitmq.commun;

import java.util.Date;

public class Helper {

	public static String getFormatedDate(){
		return Commun.dateFormat.format(new Date());
	}

	public static String generateMessage(int messageSize) {
		int begin =  ("0".getBytes())[0];
		int end = ("z".getBytes())[0];
		double interval = (double)(end - begin + 1);
		
		StringBuilder message = new StringBuilder("");
		for (int i = 0; i < messageSize; i++) {
			char letter = (char)(begin + (int)(Math.random() * interval));
			message.append(letter);
		}
		
		return message.toString();
		
	}
	
}
