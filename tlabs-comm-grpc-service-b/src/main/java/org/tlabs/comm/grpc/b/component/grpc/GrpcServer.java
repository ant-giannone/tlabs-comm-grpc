package org.tlabs.comm.grpc.b.component.grpc;

import java.io.IOException;

public interface GrpcServer {
    void start() throws IOException, InterruptedException;

    void stop();

    void blockUntilShutdown() throws InterruptedException;
}
