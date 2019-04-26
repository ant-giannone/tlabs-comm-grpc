package org.tlabs.comm.grpc.a.component;

import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tlabs.comm.grpc.a.components.grpc.GreeterGrpc;
import org.tlabs.comm.grpc.a.components.grpc.HelloReply;
import org.tlabs.comm.grpc.a.components.grpc.HelloRequest;

import java.util.concurrent.TimeUnit;

@Component
public class WelcomeComponentImpl implements WelcomeComponent {

    private static Logger LOGGER = LoggerFactory.getLogger(WelcomeComponentImpl.class);

    @Autowired
    private String welcomeMessage;

    @Autowired
    private ManagedChannel gRpcGreetingsManagedChannel;

    @Autowired
    private GreeterGrpc.GreeterBlockingStub greeterBlockingStub;

    @Autowired
    private ManagedChannel gRpcGreetingsTrustedManagedChannel;

    @Autowired
    private GreeterGrpc.GreeterBlockingStub greeterTrustedBlockingStub;


    @Override
    public void welcome() {
        LOGGER.info(welcomeMessage);
    }

    @Override
    public void sendDataToGreetingsService() throws InterruptedException, StatusRuntimeException {

        LOGGER.info("[START] :: gRPC communication to Greetings service");

        HelloRequest request = HelloRequest.newBuilder().setName("world").build();

        LOGGER.info("[PROCESSING] :: gRPC communication to Greetings service - send message");

        HelloReply response = greeterBlockingStub.sayHello(request);

        LOGGER.info("[END] :: gRPC communication to Greetings service - response from gRPC channel: {}",
                response.getMessage());

        gRpcGreetingsManagedChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    @Override
    public void sendTrustedDataToGreetingsService() throws InterruptedException, StatusRuntimeException {

        LOGGER.info("[START] :: gRPC trusted communication to Greetings service");

        HelloRequest request = HelloRequest.newBuilder().setName("world").build();

        LOGGER.info("[PROCESSING] :: gRPC trusted communication to Greetings service - send message");

        HelloReply response = greeterTrustedBlockingStub.sayHello(request);

        LOGGER.info("[END] :: gRPC trusted communication to Greetings service - response from gRPC channel: {}",
                response.getMessage());

        gRpcGreetingsTrustedManagedChannel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
}
