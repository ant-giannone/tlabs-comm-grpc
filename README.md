Overview
---

Il presente articolo illustra un utilizzo di base del protocollo gRPC e proto-buffer protocol per instaurare una 
comunicazione tra due servizi basata su protocollo binario e serializzazione/deserializzazione dei dati scambiati.

Entrambi i frameworks che stiamo adoperando sono stati realizzati in casa Google.
Di seguito i link ad entrambi i frameworks: 
    - Proto-buffer protocol:    https://developers.google.com/protocol-buffers/docs/overview
    - gRPC:                     https://grpc.io/docs/

Lo scenario descritto di seguito vede i seguenti attori coinvolti:
 - Servizio A:  gRPC client
 - Servizio B:  gRPC server
 - NGNIX:       gRPC proxy-pass

Emtrambi i servizi sono realizzati come applicazioni Spring-boot ma non fanno nulla di particolare in se: ciò che questo articolo vuole illustrare è la comunicazione tra le parti coinvolte, che nello specifico avviene attraverso SSL/TLS.

Il deploy degli attori coinvolti avviene attraverso container docker, organizzati tra loro attraverso un file docker-compose.


### Note:
Tenete conto del fatto che alcune finezze o processi di automazione potrebbero non essere stati appliati, non escludo migliorie future a ttu ciò che scrivo e pubblico. 

***Sono aperto a scambi di opinione e consigli sul come migliorare se non adirittura riscrivere quello che ho pubblicato. Il confronto mi è utile sia a comprendere meglio ciò che studio ed applico, si a "smontare" se è necessario alcune mie convinzioni che magari potrei scoprire essere errate.***


Qualche parola sul cosa stiamo adoperando
---

#### Proto-buffer protocol

Esso è descritto come uno strumento che permette la serializzazione di dati strutturati in modo più flessibile e veloce rispetto alla serializzazione XML. Una volta descritto secondo sintassi "proto" i vostri dati e servizi, vengono generati i componenti necessari per effettuare in modo semplice sia la lettura che la scrittura dei dati stessi, sia da stream che da linguaggi differenti. 

Tra le altre cose tutto è pensato per accogliere senza problemi la repocompatibilità del dato: ciò permette alle vecchie applicazione di continuare ad adoperare le vecchie versioni di struttura del dato senza che le nuove modifice a quasta ultima rischino di mandare in crash gli applicativi stessi. La sintassi "proto" rimanda un pò a quella JSON ma con alcune differenze, ad esempio possiamo
descrivere un dato strutturato come la scheda di un libro nel seguente modo:

```proto
message Book {
    required string title = 1;
    optional string description = 2;
    required string author = 3;
}
```
La struttura è semplice, abbiamo uno o più campi univoci e numerati. Ogni campo ha un nome ed un valore il cui tipo 
può essere: integer, floating-point, booleans, strings, raw bytes oppure ulteriri dati strutturati per la definizione di 
strutture gerarchiche. 

I messaggi e servizi vengono salvati su file la cui estensione è ".proto". 
Teniamo sempre a mente che la descrizione della sintassi "proto" esiste per rendere human-friendly la lettura della struttura dati: proto-buffer by default encoda il dato in formato binario e non nasce come rappresentazione testuale del dato così come accade per XML e JSON.

Per avere maggiori chiarimenti in merito all'argomento dell'encoding consiglio il seguente link: 
https://developers.google.com/protocol-buffers/docs/encoding


#### gRPC

Attraverso gRPC un applicazione client può invocare metodi su di un applicazione server che risiede su di una differente macchina come se il componente che invoca sia un oggetto locale all'enviruvment sul quale sta operando. Come molti sistemi su base RPC, anche gRPC si basa sull'idea di definire dei servizi specificandone i metodi che possono essere invocati da remoto ed ai quali vengono inviati i parametri richiesti ed ottenendo un parametro in uscita come risposta. Da un punto di vista dell'entità server, questoultimo implementa l'interfaccia che il clienti può invocare da remoto. gRPC adopera protobuf come protocollo di serializzazione e desarializzazione
dei messaggi scambiati tra client e server, quindi di fatto protobuffer è l'IDL adoperato da gRPC con il quale sono definite interfacce dei servizi e strutture dati di scambio.

gRPC permette comunicazione tra client e server in termini unari oppure di stream: 
    - il primo caso il client invia una singola richiesta al server e questo ultimo risponde con una response, come se fosse l'esecuzione di una chiamata a funzione
    - nel secondo case sia client che server possono essere predisposti per inviare e ricevere i modo continuo una serie di messaggi finchè questi continuano a transitare. gRPC garantisce l'ordine dei messaggi all'interno di una chiamata RPC individuale. Lo streaming può avvenire lato client, solo lato server oppure può essere bidirezionale

Maggiori dettagli in merito li trovate a questo link: https://grpc.io/docs/guides/concepts/

gRPC supporta anche diversi meccanismi di autenticazione:
    - SSL/TLS: gRPC possiede questo tipo di inetgrazione ed è il metodo consigliato
    - Token-based authentication with Google: OAuth2 quando si accede ad API Google attraverso gRPC
    - Estensioni a Auth parti terze: gRPC prevede un API per la creazione di plugin di integrazione con altri meccanismi di auth

Maggiori dettagli in merito li trovate a questo link: https://grpc.io/docs/guides/auth/


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