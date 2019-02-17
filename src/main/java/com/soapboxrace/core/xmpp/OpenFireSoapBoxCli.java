package com.soapboxrace.core.xmpp;

import com.soapboxrace.core.bo.ParameterBO;
import com.soapboxrace.jaxb.util.MarshalXML;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Startup
@Singleton
public class OpenFireSoapBoxCli {

	@EJB
	private ParameterBO parameterBO;

	@EJB
	private IHandshake handshake;

	private IOpenFireTalk xmppTalk;

	@PostConstruct
	public void init() {
		this.xmppTalk = handshake.getXmppTalk();
	}

	private void restart() {
		handshake.init();
		this.xmppTalk = handshake.getXmppTalk();
	}

	public void send(String msg, String to) {
		if (xmppTalk.getSocket().isClosed()) {
			restart();
		}
		xmppTalk.send(msg, to, parameterBO);
	}

	public void send(String msg, Long to) {
		if (xmppTalk.getSocket().isClosed()) {
			restart();
		}
		xmppTalk.send(msg, to, parameterBO);
	}

	public void send(Object object, Long to) {
		String responseXmlStr = MarshalXML.marshal(object);
		this.send(responseXmlStr, to);
	}

	public void setXmppTalk(IOpenFireTalk xmppTalk) {
		this.xmppTalk = xmppTalk;
	}

}