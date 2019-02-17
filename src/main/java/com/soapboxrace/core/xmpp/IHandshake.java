package com.soapboxrace.core.xmpp;

/**
 * Base interface for XMPP handshake.
 * This is necessary because reasons.
 */
public interface IHandshake
{
    void init();

    IOpenFireTalk getXmppTalk();
}