package com.capgemini.protorabbitmq.commun;

import java.util.Date;

public class Helper {

	public static String getFormatedDate(){
		return Commun.dateFormat.format(new Date());
	}
}
