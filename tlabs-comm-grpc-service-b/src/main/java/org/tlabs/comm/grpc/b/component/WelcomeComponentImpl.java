package org.tlabs.comm.grpc.b.component;

import io.grpc.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class WelcomeComponentImpl implements WelcomeComponent {

    private static Logger LOGGER = LoggerFactory.getLogger(WelcomeComponentImpl.class);

    @Autowired
    private String welcomeMessage;

    @Autowired
    private Server gRpcServer;

    public void welcome() {
        LOGGER.info(welcomeMessage);
    }


    @Override
    public void startToListen() throws IOException, InterruptedException {

        gRpcServer.start();
        gRpcServer.awaitTermination();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                gRpcServer.shutdown();
                System.err.println("*** server shut down");
            }
        });
    }
}
