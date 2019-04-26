package org.tlabs.comm.grpc.a.component;

public interface WelcomeComponent {

    public void welcome();

    void sendDataToGreetingsService() throws InterruptedException;
}
