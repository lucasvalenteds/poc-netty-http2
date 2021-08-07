# POC: Netty HTTP/2

It demonstrates how to implement HTTP/2 server and client using Netty.

The goal is to use Netty APIs to implement both client and server that communicates with each other using HTTP/2 with SSL. The SSL certificate should be self-signed and created automatically on server startup.

We want the implementation to be mostly asynchronous and non-blocking, the API should be extensible and more friendly than using Netty directly. It should demonstrate the basis of popular HTTP libraries such as Spring WebClient and Java HttpClient. Replacing those is a non-goal though.

Usage examples available at [ClientExample](src/main/java/com/example/ClientExample.java), [ServerExample](src/main/java/com/example/ServerExample.java) and [IntegrationTest](src/test/java/com/example/IntegrationTest.java).

## How to run

| Description | Command |
| :--- | :--- |
| Run tests | `./gradlew test` |
| Run server | `./gradlew runServer` |
| Run client | `./gradlew runClient` |

## Preview

```
$ ./gradlew runServer

> Task :runServer
[main] INFO com.example.server.Http2Server - HTTP/2 server is listening on https://127.0.0.1:8443/

```

```
$ ./gradlew runClient

> Task :runClient
[main] INFO com.example.client.Http2Client - Client connected to the server at https://127.0.0.1:8443/
[main] INFO com.example.client.Http2Client - Response: Hello World
```

