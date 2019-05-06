package org.tlabs.comm.grpc.a.runner;

import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.tlabs.comm.grpc.a.component.WelcomeComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class MainRunner implements CommandLineRunner {

    private static Logger LOGGER = LoggerFactory.getLogger(MainRunner.class);

    @Autowired
    private WelcomeComponent welcomeComponent;

    @Override
    public void run(String... args) {

        welcomeComponent.welcome();

        List<Thread> threads = new ArrayList<>();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                try {

                    welcomeComponent.sendTrustedDataToGreetingsService();
                }catch(StatusRuntimeException e) {

                    LOGGER.error("An error occurred: {}", e.getStatus().getDescription());
                } catch (InterruptedException e) {

                    LOGGER.error("An error occurred: {}", e.getMessage());
                }
            }
        };

        Random random = new Random();

        for(int i=0; i<45; i++) {

            threads.add(new Thread(runnable));

            try {

                Thread.sleep(random.nextInt(8000)+8000);
                threads.get(i).start();
            } catch (InterruptedException e) {

                LOGGER.error("An error occurred on sleep setting: {}", e.getMessage());
            }
        }
    }
}
