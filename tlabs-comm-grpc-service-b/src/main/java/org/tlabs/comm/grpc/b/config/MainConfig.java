package org.tlabs.comm.grpc.b.config;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tlabs.comm.grpc.b.component.GreeterImpl;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MainConfig {

    private static Logger LOGGER = LoggerFactory.getLogger(MainConfig.class);

    @Value("${org.tlabs.comm.grpc.b.common.message.welcome}")
    private String welcomeMessage;

    @Value("${org.tlabs.comm.grpc.b.env.message.welcome}")
    private String welcomeMessageOnEnv;

    @Value("${org.tlabs.comm.grpc.b.service.greetings.host}")
    private String gRpcGreetingsHost;

    @Value("${org.tlabs.comm.grpc.b.service.greetings.port}")
    private String gRpcGreetingsPort;

    @Value("${org.tlabs.comm.grpc.b.service.greetings.trusted.host}")
    private String gRpcGreetingsTrustedHost;

    @Value("${org.tlabs.comm.grpc.b.service.greetings.trusted.port}")
    private String gRpcGreetingsTrustedPort;

    @Value("${org.tlabs.comm.grpc.a.service.greetings.trusted.certs.folder}")
    private String gRpcTrustedCertsFolder;

    @Value("${org.tlabs.comm.grpc.b.service.greetings.trusted.certs.key}")
    private String gRpcTrustedKey;

    @Value("${org.tlabs.comm.grpc.b.service.greetings.trusted.certs.cert}")
    private String gRpcTrustedCert;


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

    @Bean
    public Server gRpcTrustedServer() {

        Path gRpcTrustedKeyPath = Paths.get(gRpcTrustedCertsFolder, gRpcTrustedKey);
        Path gRpcTrustedCertPath = Paths.get(gRpcTrustedCertsFolder, gRpcTrustedCert);

        LOGGER.info("\ngRpcGreetingsTrustedPort: {};", gRpcGreetingsTrustedPort);

        return ServerBuilder.forPort(Integer.parseInt(gRpcGreetingsTrustedPort))
                .useTransportSecurity(gRpcTrustedCertPath.toFile(), gRpcTrustedKeyPath.toFile())
                .addService(new GreeterImpl())
                .build();
    }
}
