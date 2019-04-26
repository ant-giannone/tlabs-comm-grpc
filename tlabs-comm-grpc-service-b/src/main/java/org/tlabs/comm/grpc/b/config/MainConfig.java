package org.tlabs.comm.grpc.b.config;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tlabs.comm.grpc.b.component.GreeterImpl;

@Configuration
public class MainConfig {

    @Value("${org.tlabs.comm.grpc.b.common.message.welcome}")
    private String welcomeMessage;

    @Value("${org.tlabs.comm.grpc.b.env.message.welcome}")
    private String welcomeMessageOnEnv;

    @Value("${org.tlabs.comm.grpc.b.service.greetings.host}")
    private String gRpcGreetingsHost;

    @Value("${org.tlabs.comm.grpc.b.service.greetings.port}")
    private String gRpcGreetingsPort;


    @Bean
    public String welcomeMessage() {

        return (new StringBuilder()).append("\n")
                .append(welcomeMessage).append("\n")
                .append(welcomeMessageOnEnv).toString();
    }

    @Bean
    public Server gRpcServer() {
        return ServerBuilder.forPort(Integer.parseInt(gRpcGreetingsPort))
                .addService(new GreeterImpl())
                .build();
    }
}
