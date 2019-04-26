# tlabs-comm-grpc
gRPC basic communication between two spring-boot apps


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