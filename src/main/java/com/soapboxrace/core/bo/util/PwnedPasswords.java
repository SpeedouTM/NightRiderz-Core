package com.soapboxrace.core.bo.util;

import com.google.common.hash.Hashing;

import javax.ws.rs.client.ClientBuilder;
import java.nio.charset.Charset;

public class PwnedPasswords {
    public static int checkHash(String hash) {
        hash = hash.toUpperCase();
        String resp = ClientBuilder.newClient().target("https://api.pwnedpasswords.com/range/"+hash.substring(0, 5)).request().get(String.class);
        for (String line : resp.split("\r\n")) {
            if (line.isEmpty()) continue;
            String[] halves = line.split(":");
            if (halves[0].equals(hash.substring(5))) {
                return Integer.parseInt(halves[1]);
            }
        }
        return 0;
    }

    public static int checkPassword(String password) {
        @SuppressWarnings("deprecation")
        String hash = Hashing.sha1().hashString(password, Charset.defaultCharset()).toString();
        return checkHash(hash);
    }
}
