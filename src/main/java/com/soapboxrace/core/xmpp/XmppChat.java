package com.soapboxrace.core.xmpp;

import com.google.common.xml.XmlEscapers;

public class XmppChat
{
    public static String createSystemMessage(String message)
    {
        String escapedMessage = XmlEscapers.xmlContentEscaper().escape(message);
        return String.format("<response status='1' ticket='0'>\n" +
                "<ChatBroadcast>\n" +
                "<ChatBlob>\n" +
                "<FromName>System</FromName>\n" +
                "<FromPersonaId>0</FromPersonaId>\n" +
                "<FromUserId>0</FromUserId>\n" +
                "<Message>%s</Message>\n" +
                "<ToId>0</ToId>\n" +
                "<Type>2</Type>\n" +
                "</ChatBlob>\n" +
                "</ChatBroadcast>\n" +
                "</response>", escapedMessage);
    }
}
