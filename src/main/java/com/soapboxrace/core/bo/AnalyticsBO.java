package com.soapboxrace.core.bo;

import com.soapboxrace.core.jpa.UserEntity;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;

@Singleton
@Startup
public class AnalyticsBO {
    private String analyticsId;
    private Boolean analyticsEnabled;
    private String address;

    @EJB
    private ParameterBO parameterBO;

    @PostConstruct
    public void init() {
        analyticsId = parameterBO.getStrParam("ANALYTICS_ID");
        analyticsEnabled = analyticsId != null;
        address = parameterBO.getStrParam("SERVER_ADDRESS");
        if (!address.endsWith("/")) {
            address += "/";
        }
    }

    public void sendEvent(String category, String action, UserEntity user) {
        sendEvent(category, action, null, user);
    }

    public void sendEvent(String category, String action, String label, UserEntity user) {
        if (!analyticsEnabled) return;

        String body = "v=1&t=event&ec=" +
                category +
                "&ea=" +
                action +
                "&tid=" +
                analyticsId +
                "&uid=" +
                user.getId() +
                "&uip=" +
                user.getIpAddress() +
                "&cd1=" +
                user.getUserAgent();
        if (label != null) {
            body += "&el=" + label;
        }
        ClientBuilder.newClient()
                .target("https://www.google-analytics.com")
                .path("/collect")
                .request()
                .post(Entity.text(body));
    }

    public void sendPageView(UserEntity user) {
        if (!analyticsEnabled) return;

        String body = "v=1&t=pageview&dl=" +
                address +
                "&tid=" +
                analyticsId +
                "&uid=" +
                user.getId() +
                "&uip=" +
                user.getIpAddress() +
                "&cd1=" +
                user.getUserAgent();
        ClientBuilder.newClient()
                .target("https://www.google-analytics.com")
                .path("/collect")
                .request()
                .post(Entity.text(body));
    }

    public void trackUserLogin(UserEntity user) {
        sendPageView(user);
    }

    public void trackUserRegister(UserEntity user) {
        sendPageView(user);
    }
}
