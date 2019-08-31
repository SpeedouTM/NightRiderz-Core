package com.soapboxrace.jaxb.util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.util.HashMap;
import java.util.Map;

public class JAXBContextCache {
    private static Map<Class<?>, JAXBContext> cache = new HashMap<>();

    public static JAXBContext get(Class<?> cls) throws JAXBException {
        JAXBContext context = cache.get(cls);
        if (context == null) {
            context = JAXBContext.newInstance(cls);
            cache.put(cls, context);
        }
        return context;
    }
}
