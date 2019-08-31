package com.soapboxrace.core.api.util;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;

public class VersionUtil {
    public static String getVersionHash() {
        try {
            return "frss-sbrw/" + Resources.toString(Resources.getResource("version.txt"), Charsets.UTF_8);
        } catch (IOException e) {
            return "frss-sbrw/UNKNOWN";
        }
    }
}
