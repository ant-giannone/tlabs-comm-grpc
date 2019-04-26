package org.tlabs.comm.grpc.a.component;

import io.grpc.StatusRuntimeException;

public interface WelcomeComponent {

    public void welcome();

    void sendDataToGreetingsService() throws InterruptedException;

    void sendTrustedDataToGreetingsService() throws InterruptedException, StatusRuntimeException;
}
