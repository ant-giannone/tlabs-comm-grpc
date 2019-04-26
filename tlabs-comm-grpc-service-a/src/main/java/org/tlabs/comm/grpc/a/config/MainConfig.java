package org.tlabs.comm.grpc.a.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tlabs.comm.grpc.a.components.grpc.GreeterGrpc;

@Configuration
public class MainConfig {

    private static Logger LOGGER = LoggerFactory.getLogger(MainConfig.class);

    @Value("${org.tlabs.comm.grpc.a.common.message.welcome}")
    private String welcomeMessage;

    @Value("${org.tlabs.comm.grpc.a.env.message.welcome}")
    private String welcomeMessageOnEnv;

    @Value("${org.tlabs.comm.grpc.a.service.greetings.host}")
    private String gRpcGreetingsHost;

    @Value("${org.tlabs.comm.grpc.a.service.greetings.port}")
    private String gRpcGreetingsPort;


    @Bean
    public String welcomeMessage() {

        return (new StringBuilder()).append("\n")
                .append(welcomeMessage).append("\n")
                .append(welcomeMessageOnEnv).toString();
    }

    @Bean
    public ManagedChannel gRpcGreetingsManagedChannel() {

        return ManagedChannelBuilder
                .forAddress(gRpcGreetingsHost, Integer.parseInt(gRpcGreetingsPort))
                .usePlaintext()
                .build();
    }

    @Bean
    public GreeterGrpc.GreeterBlockingStub greeterBlockingStub() {

        return GreeterGrpc.newBlockingStub(gRpcGreetingsManagedChannel());
    }
}
