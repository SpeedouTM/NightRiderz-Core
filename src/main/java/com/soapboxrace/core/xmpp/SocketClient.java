package com.soapboxrace.core.xmpp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClient
{

    final Logger logger = LoggerFactory.getLogger(SocketClient.class);

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public SocketClient(String srvAddress, int port)
    {
        int tries = 0;
        while (true)
        {
            if (tries >= 20)
            {
                logger.error("Failed to connect to XMPP host");
                System.exit(1);
                break;
            }

            try
            {
                tries++;
                logger.info("Attempting to connect to OpenFire XMPP Host. Attempt #" + String.valueOf(tries));
                socket = new Socket(srvAddress, port);
                out = new PrintWriter(this.socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                logger.info("Connected to OpenFire XMPP Host...");
                break;
            } catch (Exception e)
            {
                logger.error("Failed, retrying.");
                logger.error(e.toString());
                try
                {
                    Thread.sleep(1000);
                } catch (InterruptedException j)
                {
                    j.printStackTrace();
                }
            }
        }
    }

    public void send(String command)
    {
        System.out.println("C->S [" + command + "]");
        out.println(command);
        out.flush();
    }

    public String receive()
    {
        String receive = "";
        try
        {
            char[] cbuf = new char[10240];
            in.read(cbuf);
            receive = new String(cbuf);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        System.out.println("S->C [" + receive + "]");
        return receive;
    }

    public Socket getSocket()
    {
        return socket;
    }

}