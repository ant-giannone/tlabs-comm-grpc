Overview
---

This article illustrates a basic use of the gRPC protocol and proto-buffer protocol to establish a  
communication between two services based on binary protocol and serialization / deserialization of the data exchanged.

Both frameworks we are using have been made at Google.  
Below are links to both frameworks: 
- **Proto-buffer protocol:** https://developers.google.com/protocol-buffers/docs/overview 
- **gRPC:** https://grpc.io/docs/


The scenario described below sees the following actors involved:

- **Service A:** gRPC client
- **Service B:** gRPC server
- **NGNIX:** gRPC proxy-pass

Both services are implemented as Spring-boot applications but do nothing in particular:  
what this article wants to illustrate is the communication between the components involved,  
which specifically takes place through SSL / TLS.

The deployment of the actors involved takes place through docker containers,  
organized between them through a docker-compose file.
---

##### Note
Keep in mind that what I write at the moment is minimal and  
I do not exclude future improvements to everything I write and public.
   
***I am open to exchanges of views and appreciate receiving advice to improve or rewrite what I have published.  
The comparison with the community is useful both to better understand what I study and apply,  
but it is also useful to "disassemble" some of my beliefs that I may discover to be incorrect.***


A few notes on what we are using
---

**Proto-buffer protocol**

It is described as a tool that allows the serialization of structured data in a more flexible and faster  
way than XML serialization. Once you have described your data and services according to syntax "proto",  
the necessary components are generated to perform both reading and writing of data in a simple way.  
The reading and writing modalities between client and server can take place either as a single invocation or  
treated as a data stream.

Among other things, it is designed to easily accommodate data backward compatibility: this allows the old applications  
to continue to use the old data structure versions without the new modifications threatening to cause application crashes.  
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
---

**gRPC**

Through gRPC a client application can transparently invoke methods of a server application that resides on a different machine,  
using component methods such as local calls. Like many RPC-based systems, gRPC is also based on the idea of ​​defining services  
by specifying the methods that can be invoked remotely and which parameters are required to obtain an outgoing response.  
From a server point of view, it implements the interface that the client can invoke remotely.  
gRPC uses protobuf as a serialization and desarialization protocol for the messages exchanged.  
In fact protobuffer is the IDL used by gRPC for the definition of the interfaces of the services and the data structures exchanged.

gRPC allows communication between client and server in unary or stream terms: 
- in the first case the client sends a single request to the server and it responds with an outgoing data,  
as if the execution were a call to a local method
- in the second case both client and server can be set up to continuously send and receive a series of data until  
they continue to transit. gRPC guarantees the order of messages within an individual RPC call.  
Communication on a stream basis can take place on the client side, only on the server side or can be bidirectional

More details can be found at this link: https://grpc.io/docs/guides/concepts/

gRPC also supports different authentication mechanisms:
- SSL / TLS: gRPC has this type of integration and is the recommended method
- Token-based authentication with Google: OAuth2 when accessing Google API through gRPC
- Extensions to third-party Auths: gRPC provides an API for the creation of integration  
plugins with other auth mechanisms

More details can be found at this link: https://grpc.io/docs/guides/auth/


Come eseguire build e deploy
---


Cosa abbiamo realizzato
---

### gPRC communication by TLS
---

openssl command to generate .pem and .crt files
```
openssl req -x509 -newkey rsa:1024 -keyout ./my-test-key.pem -out ./my-test-cert.crt -days 999 -subj "/CN=localhost"
```

openssl command to convert .pem file into PKCS8 format
```
openssl pkcs8 -topk8 -nocrypt -in my-test-key.pem -out my-test-key-PKCS8.pem
```