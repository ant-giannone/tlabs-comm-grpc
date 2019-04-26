package org.tlabs.comm.grpc.b.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.tlabs.comm.grpc.b.component.WelcomeComponent;

@Component
public class MainRunner implements CommandLineRunner {

    @Autowired
    private WelcomeComponent welcomeComponent;

    @Override
    public void run(String... args) throws Exception {
        welcomeComponent.welcome();
        welcomeComponent.startToListenTrusted();
    }
}