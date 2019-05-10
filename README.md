Overview
---

This article illustrates a basic usage of the gRPC protocol and proto-buffer protocol to establish a  
communication between two services, based on binary protocol and serialization / deserialization of the data exchanged.

Both frameworks we are using have been made at Google.  
Below are links to both frameworks: 
- **Proto-buffer protocol:** https://developers.google.com/protocol-buffers/docs/overview 
- **gRPC:** https://grpc.io/docs/


The scenario described below sees the following actors involved:

- **Service A:** gRPC client
- **Service B:** gRPC server
- **NGNIX:** gRPC proxy-pass

Both services are implemented as Spring-boot applications but do nothing in particular:  
this article illustrates the communication between the components involved,  
which specifically takes place through SSL/TLS.

The deployment of the actors involved takes place through docker containers,  
organized between them through a docker-compose file.


##### Note
---
Keep in mind that what I wrote at the moment is minimal and  
I do not exclude future improvements to everything I write and public.
   
***I am open to exchanges of views and appreciate receiving advice to improve or rewrite what I have published.  
The meeting with the community is useful both to better understand what I study and apply,  
but it is also useful to "disassemble" some of my beliefs that I may discover to be incorrect.***
##### End Note :) 
---

A few notes on what we are using
---

**Proto-buffer protocol**

It is described as a tool that allows the serialization of structured data in a more flexible and faster  
way than XML serialization. Once you have described your data and services according to syntax "proto",  
the necessary components are generated to perform both reading and writing of data in a simple way.  
The reading and writing modalities between client and server can take place either as a single invocation or  
treated as a data stream.

Among other things, it is designed to easily accommodate data backward compatibility: this allows the old applications  
to continue to use the old data structure versions without crashes.  
The "proto" syntax refers a bit to the JSON syntax but with some differences, for example we can describe a data structured  
in the following way:

```proto
message Book {
    required string title = 1;
    optional string description = 2;
    required string author = 3;
}
```

The structure is simple, we have one or more unique and numbered fields. Each field has a name and a value.  
The allowed types are the following: integer, floating-point, booleans, strings, raw bytes or nested structured  
data for the definition of hierarchical structures.

Messages and services are saved on files whose extension is ".proto". 
We always keep in mind that the description of the "proto" syntax exists to make the reading  
of the data structure human-friendly: proto-buffer by default encodes the data in binary format and  
it was not born as a textual representation of the data as happens for XML or JSON.

To get more clarification on the subject of the encoding, I recommend the following link: 
https://developers.google.com/protocol-buffers/docs/encoding


**gRPC**

Through gRPC a client application can transparently invoke methods of a server application that resides on a different machine,  
using component methods such as local calls. Like many RPC-based systems, gRPC is also based on the idea of ​​defining services  
by specifying the methods that can be invoked remotely and which parameters are required to obtain an outgoing response.  
From a server point of view, it implements the interface that the client can invoke remotely.  
gRPC uses protobuf as serialization and desarialization protocol for the messages exchanged.  
In fact protobuf is the IDL used by gRPC for the definition of the interfaces.

gRPC allows communication between client and server in unary or stream terms: 
- in the first case the client sends a single request to the server and it responds with an outgoing data,  
as if the execution happen as a call to a local method
- in the second case both client and server can be set up to continuously send and receive a series of data until  
they continue to transit. gRPC guarantees the order of messages within an individual RPC call.  
Communication on a stream basis can take place on the client side, only on the server side or can be bidirectional

More details can be found at this link: https://grpc.io/docs/guides/concepts/

gRPC also supports different authentication mechanisms:
- SSL/TLS: gRPC has this type of integration and it is the recommended method
- Token-based authentication with Google: OAuth2 when accessing Google API through gRPC
- Extensions to third-party Auth mechanism: gRPC provides an API for the creation of integration  
plugins with other auth mechanisms

More details can be found at this link: https://grpc.io/docs/guides/auth/


How to build and deploy
---

First to all we need to clone the repo and move into main project folder

```bash
git clone https://github.com/ant-giannone/tlabs-comm-grpc.git
```

We have a maven project structured as parent-pom project with two modules: 

- tlabs-comm-grpc-service-a
- tlabs-comm-grpc-service-b

We also have a folder, "shared", that contains ngnix config and fake certificates

To build the project, digit the following command:

```bash
mvn clean package
```

To deploy all as docker containers, execute the following command:

```bash
docker-compose -f docker-compose.yml up -d
```

The docker compose file is organized to create:
- three server instances(b1/b2/b3),
- an NGNIX that executes balancing and proxy-pass of the gRPC requests
- a client instance that invoke gRPC calls in direction to NGNIX and receive a response by 
one of the three server instances balanced by NGNIX

You can see the logs with the following command:

```bash
docker logs -f <container-name> 

docker logs -f service-a

docker logs -f service-b1
```

or use your integration IDE, I'm using IntelliJ and docker integration

If you verify logs for all three server instances, you can see the logs that prints what IP create response for client request


What we have achieved
---

#### Key and Certificate
Well, first I reports the command used to generate fake key and certificate(.pem; .crt)

```
openssl req -x509 -newkey rsa:1024 -keyout ./my-test-key.pem -out ./my-test-cert.crt -days 999 -subj "/CN=ngnix"
```
Note: attention to create all with the "common name" correct for host, in this case the container_name of ngnix **/CN=ngnix**

Well, now digit openssl command to convert .pem file into PKCS8 format
```
openssl pkcs8 -topk8 -nocrypt -in my-test-key.pem -out my-test-key-PKCS8.pem
```

#### Protobuf service definition file

I'm using proto file from Google example

```bash
// The greeting service definition.
service Greeter {
    // Sends b greeting
    rpc SayHello (HelloRequest) returns (HelloReply) {}
}

// The request message containing the user's name.
message HelloRequest {
    string name = 1;
}

// The response message containing the greetings
message HelloReply {
    string message = 1;
}
```

It's used to define services to invoke and data structure to use.  
The maven pom file contains the plugin used to compile and build all  
components used for client stub and services implementations

```java
<plugin>
    <groupId>org.xolstice.maven.plugins</groupId>
    <artifactId>protobuf-maven-plugin</artifactId>
    <version>0.5.1</version>
    <configuration>
        <protocArtifact>com.google.protobuf:protoc:${com.google.protobuf.version}:exe:${os.detected.classifier}</protocArtifact>
        <pluginId>grpc-java</pluginId>
        <pluginArtifact>io.grpc:protoc-gen-grpc-java:${io.grpc.version}:exe:${os.detected.classifier}</pluginArtifact>
        <protoSourceRoot>
            ${basedir}/src/main/resources/proto
        </protoSourceRoot>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>compile</goal>
                <goal>compile-custom</goal>
            </goals>
        </execution>
    </executions>
</plugin>

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-enforcer-plugin</artifactId>
    <version>1.4.1</version>
    <executions>
        <execution>
            <id>enforce</id>
            <goals>
                <goal>enforce</goal>
            </goals>
            <configuration>
                <rules>
                    <requireUpperBoundDeps/>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

#### Service A

How to create gRPC channel in plain text

```java

    @Bean
    public ManagedChannel gRpcGreetingsManagedChannel() {

        LOGGER.info("\ngRpcGreetingsHost: {};\ngRpcGreetingsPort: {}", gRpcGreetingsHost, gRpcGreetingsPort);

        return ManagedChannelBuilder
                .forAddress(gRpcGreetingsHost, Integer.parseInt(gRpcGreetingsPort))
                .usePlaintext()
                .build();
    }
```

How to create gRPC channel with ssl/tls
```java
@Bean
public ManagedChannel gRpcGreetingsTrustedManagedChannel() throws SSLException {

    Path gRpcTrustedCertPath = Paths.get(gRpcTrustedCertsFolder, gRpcTrustedCert);

    return NettyChannelBuilder.forAddress(gRpcGreetingsTrustedHost, Integer.parseInt(gRpcGreetingsTrustedPort))
            .sslContext(GrpcSslContexts.forClient().trustManager(gRpcTrustedCertPath.toFile()).build())
            .build();
}
```

How to create the bean that permit injection of the client-stub for gRPC call invocation.  
In this case we use **BlockingStub**, then we are using the client-side **unary** and blocking gRPC invocation call
```java

    @Bean
    public GreeterGrpc.GreeterBlockingStub greeterTrustedBlockingStub() throws SSLException {

        return GreeterGrpc.newBlockingStub(gRpcGreetingsTrustedManagedChannel());
    }
```

Here, you can see the snipped code that simulates many request from client

```java

List<Thread> threads = new ArrayList<>();

Runnable runnable = new Runnable() {
    @Override
    public void run() {

        try {

            welcomeComponent.sendTrustedDataToGreetingsService();
        }catch(StatusRuntimeException e) {

            LOGGER.error("An error occurred: {}", e.getStatus().getDescription());
        } catch (InterruptedException e) {

            LOGGER.error("An error occurred: {}", e.getMessage());
        }
    }
};

Random random = new Random();

for(int i=0; i<45; i++) {

    threads.add(new Thread(runnable));

    try {

        Thread.sleep(random.nextInt(8000)+8000);
        threads.get(i).start();
    } catch (InterruptedException e) {

        LOGGER.error("An error occurred on sleep setting: {}", e.getMessage());
    }
}
```

#### Service B

Greetings gRPC service implementation and annotated as Spring component

```java
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
```

#### The server: how to create it and how it is integrated into spring IoC

All gRPC services extends an abstract class that implements io.grpc.BindableService interface,
so we can inject all services that implements same interface as follow, in you own spring component:

```java

@Component
public class GrpcServerImpl implements GrpcServer {

    @Autowired
    protected List<BindableService> grpcServers;
}
```

Well, now we can create gRPC server and register all defined services as follow:

```java

    @Override
    public void start() throws IOException, InterruptedException {

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
```

Notes
---
gRPC channel creation is expensive, so we need to create the channle one time and during shutdown app we can destroy correctly the channel. We never create and destroy the channel any time, we use a channel per application!

```java

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
```
