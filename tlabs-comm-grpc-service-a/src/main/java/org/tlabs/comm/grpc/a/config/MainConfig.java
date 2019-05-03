package org.tlabs.comm.grpc.a.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tlabs.comm.grpc.a.components.grpc.GreeterGrpc;

import javax.net.ssl.SSLException;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    @Value("${org.tlabs.comm.grpc.a.service.greetings.trusted.host}")
    private String gRpcGreetingsTrustedHost;

    @Value("${org.tlabs.comm.grpc.a.service.greetings.trusted.port}")
    private String gRpcGreetingsTrustedPort;

    @Value("${org.tlabs.comm.grpc.a.service.greetings.trusted.certs.folder}")
    private String gRpcTrustedCertsFolder;

    @Value("${org.tlabs.comm.grpc.a.service.greetings.trusted.certs.cert}")
    private String gRpcTrustedCert;


    @Bean
    public String welcomeMessage() {

        return (new StringBuilder()).append("\n")
                .append(welcomeMessage).append("\n")
                .append(welcomeMessageOnEnv).toString();
    }

    @Bean
    public ManagedChannel gRpcGreetingsManagedChannel() {

        LOGGER.info("\ngRpcGreetingsHost: {};\ngRpcGreetingsPort: {}", gRpcGreetingsHost, gRpcGreetingsPort);

        return ManagedChannelBuilder
                .forAddress(gRpcGreetingsHost, Integer.parseInt(gRpcGreetingsPort))
                .usePlaintext()
                .build();
    }

    @Bean
    public ManagedChannel gRpcGreetingsTrustedManagedChannel() throws SSLException {

        Path gRpcTrustedCertPath = Paths.get(gRpcTrustedCertsFolder, gRpcTrustedCert);

        LOGGER.info("\ngRpcGreetingsTrustedHost: {};\ngRpcGreetingsTrustedPort: {}", gRpcGreetingsTrustedHost, gRpcGreetingsTrustedPort);

        return NettyChannelBuilder.forAddress(gRpcGreetingsTrustedHost, Integer.parseInt(gRpcGreetingsTrustedPort))
                .sslContext(GrpcSslContexts.forClient().trustManager(gRpcTrustedCertPath.toFile()).build())
                .build();
    }

    @Bean
    public GreeterGrpc.GreeterBlockingStub greeterBlockingStub() {

        return GreeterGrpc.newBlockingStub(gRpcGreetingsManagedChannel());
    }

    @Bean
    public GreeterGrpc.GreeterBlockingStub greeterTrustedBlockingStub() throws SSLException {

        return GreeterGrpc.newBlockingStub(gRpcGreetingsTrustedManagedChannel());
    }
}
