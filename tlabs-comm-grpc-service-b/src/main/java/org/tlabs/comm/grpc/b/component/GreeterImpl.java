package org.tlabs.comm.grpc.b.component;

import io.grpc.stub.StreamObserver;

public class GreeterImpl extends org.tlabs.comm.grpc.b.components.grpc.GreeterGrpc.GreeterImplBase {

    @Override
    public void sayHello(org.tlabs.comm.grpc.b.components.grpc.HelloRequest req, StreamObserver<org.tlabs.comm.grpc.b.components.grpc.HelloReply> responseObserver) {
        org.tlabs.comm.grpc.b.components.grpc.HelloReply reply = org.tlabs.comm.grpc.b.components.grpc.HelloReply.newBuilder().setMessage("Hello " + req.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
