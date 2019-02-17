package com.soapboxrace.core.bo;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import de.mkammerer.argon2.Argon2Helper;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton
@Startup
public class Argon2BO {
    private Argon2 argon2 = Argon2Factory.create();
    private int timeCost = 500;
    private int memoryCost = 16*1024;
    private int parallelism = 1;
    private int iterations;

    @PostConstruct
    public void init() {
        iterations = Argon2Helper.findIterations(argon2, timeCost, memoryCost, parallelism);
        System.out.format("Argon2 Init; timeCost = %d, memoryCost = %d, parallelism = %d, iterations = %d\n",
                timeCost, memoryCost, parallelism, iterations);
    }

    public String hash(String password) {
         return argon2.hash(iterations, memoryCost, parallelism, password);
    }

    public boolean verify(String password, String hash) {
        return argon2.verify(password, hash);
    }
}
