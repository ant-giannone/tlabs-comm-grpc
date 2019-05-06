package org.tlabs.comm.grpc.b.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tlabs.comm.grpc.b.component.grpc.GrpcServer;

@Component
public class ShutdownHandler implements DisposableBean {

    private static Logger LOGGER = LoggerFactory.getLogger(ShutdownHandler.class);

    @Autowired
    private GrpcServer grpcServer;

    @Override
    public void destroy() throws Exception {

        LOGGER.info("[START] :: Shutdown phase...");

        LOGGER.info("[PROCESSING] :: Shutdown phase: shutdown gRPC server");

        grpcServer.stop();

        LOGGER.info("[END] :: Shutdown phase...");
    }
}
