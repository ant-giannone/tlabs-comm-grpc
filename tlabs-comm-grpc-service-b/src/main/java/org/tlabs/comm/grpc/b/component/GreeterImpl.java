package org.tlabs.comm.grpc.b.component;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreeterImpl extends org.tlabs.comm.grpc.b.components.grpc.GreeterGrpc.GreeterImplBase {

    private static Logger LOGGER = LoggerFactory.getLogger(GreeterImpl.class);

    @Override
    public void sayHello(org.tlabs.comm.grpc.b.components.grpc.HelloRequest req, StreamObserver<org.tlabs.comm.grpc.b.components.grpc.HelloReply> responseObserver) {
        org.tlabs.comm.grpc.b.components.grpc.HelloReply reply = org.tlabs.comm.grpc.b.components.grpc.HelloReply.newBuilder().setMessage("Hello " + req.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
