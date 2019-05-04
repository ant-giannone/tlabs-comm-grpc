package org.tlabs.comm.grpc.b.component.grpc;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tlabs.comm.grpc.b.data.net.NetInfo;

@Component
public class GreeterImpl
        extends org.tlabs.comm.grpc.b.components.grpc.GreeterGrpc.GreeterImplBase
    implements GrpcService {

    private static Logger LOGGER = LoggerFactory.getLogger(GreeterImpl.class);

    @Autowired
    protected NetInfo netInfo;



    @Override
    public void sayHello(org.tlabs.comm.grpc.b.components.grpc.HelloRequest req, StreamObserver<org.tlabs.comm.grpc.b.components.grpc.HelloReply> responseObserver) {

        LOGGER.info("[START] ::  Say-Hello service response from host: {}", netInfo.toString());

        org.tlabs.comm.grpc.b.components.grpc.HelloReply reply = org.tlabs.comm.grpc.b.components.grpc.HelloReply.newBuilder().setMessage("Hello " + req.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
