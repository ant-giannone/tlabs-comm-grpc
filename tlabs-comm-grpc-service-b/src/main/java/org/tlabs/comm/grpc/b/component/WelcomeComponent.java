package org.tlabs.comm.grpc.b.component;

import java.io.IOException;

public interface WelcomeComponent {

    public void welcome();

    void startToListen() throws IOException, InterruptedException;

    void startToListenTrusted() throws IOException, InterruptedException;
}
