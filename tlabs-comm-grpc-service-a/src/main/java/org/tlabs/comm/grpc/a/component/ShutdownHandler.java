package org.tlabs.comm.grpc.a.component;

import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShutdownHandler implements DisposableBean {

    private static Logger LOGGER = LoggerFactory.getLogger(ShutdownHandler.class);

    @Autowired
    private ManagedChannel gRpcGreetingsTrustedManagedChannel;

    @Autowired
    private ManagedChannel gRpcGreetingsManagedChannel;

    @Override
    public void destroy() throws Exception {

        LOGGER.info("[START] :: Shutdown phase...");

        if(!gRpcGreetingsManagedChannel.isShutdown() && !gRpcGreetingsManagedChannel.isTerminated()) {

            LOGGER.info("[PROCESSING] :: Shutdown phase: shutdown gRPC channel");

            gRpcGreetingsManagedChannel.shutdownNow();
        }

        if(!gRpcGreetingsTrustedManagedChannel.isShutdown() && !gRpcGreetingsTrustedManagedChannel.isTerminated()) {

            LOGGER.info("[PROCESSING] :: Shutdown phase: shutdown gRPC trusted channel");

            gRpcGreetingsTrustedManagedChannel.shutdownNow();
        }

        LOGGER.info("[END] :: Shutdown phase...");
    }
}
