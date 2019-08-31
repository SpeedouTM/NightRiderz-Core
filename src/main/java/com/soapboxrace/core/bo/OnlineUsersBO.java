package com.soapboxrace.core.bo;

import com.soapboxrace.core.xmpp.OpenFireRestApiCli;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;

@Singleton
public class OnlineUsersBO {
	@EJB
	OpenFireRestApiCli openFireRestApiCli;

	private int onlineUsers;

	public int getNumberOfUsersOnlineNow() {
		return onlineUsers;
	}

	@Schedule(minute = "*", hour = "*", persistent = false)
	public void updateOnlineUsers() {
		onlineUsers = openFireRestApiCli.getTotalOnlineUsers();
	}
}
