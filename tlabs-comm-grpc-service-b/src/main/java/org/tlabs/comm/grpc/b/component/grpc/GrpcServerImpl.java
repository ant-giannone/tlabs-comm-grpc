package org.tlabs.comm.grpc.b.component.grpc;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
public class GrpcServerImpl implements GrpcServer {

    private static Logger LOGGER = LoggerFactory.getLogger(GrpcServerImpl.class);


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

    @Autowired
    protected List<BindableService> grpcServers;

    protected Server server;

    @Override
    public void start() throws IOException, InterruptedException {

        Path gRpcTrustedKeyPath = Paths.get(gRpcTrustedCertsFolder, gRpcTrustedKey);
        Path gRpcTrustedCertPath = Paths.get(gRpcTrustedCertsFolder, gRpcTrustedCert);

        LOGGER.info("\ngRpcGreetingsTrustedPort: {};", gRpcGreetingsTrustedPort);

        ServerBuilder<?> serverBuilder = ServerBuilder.forPort(Integer.parseInt(gRpcGreetingsTrustedPort))
                .useTransportSecurity(gRpcTrustedCertPath.toFile(), gRpcTrustedKeyPath.toFile());

        grpcServers.stream().forEach(bindableService -> serverBuilder.addService(bindableService));

        this.server = serverBuilder.build();

        this.server.start();
        this.server.awaitTermination();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Use stderr here since the logger may have been reset by its JVM shutdown hook.
            LOGGER.error("*** shutting down gRPC server since JVM is shutting down");
            server.shutdown();
            LOGGER.error("*** server shut down");
        }));
    }

    @Override
    public void stop() {
        if (this.server != null) {
            this.server.shutdown();
        }
    }

    /**
     * Wait for main method. the gprc services uses daemon threads
     * @throws InterruptedException
     */
    @Override
    public void blockUntilShutdown() throws InterruptedException {
        if (this.server != null) {
            this.server.awaitTermination();
        }
    }
}
