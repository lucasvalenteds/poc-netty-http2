package com.example;

import com.example.client.Http2Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.net.URI;

public final class ClientExample {

    private static final Logger logger = LoggerFactory.getLogger(Http2Client.class);

    public static void main(String[] args) {
        var uri = URI.create("https://127.0.0.1:8443/");

        try (var client = new Http2Client(uri)) {
            logger.info("Client connected to the server at {}", uri);

            client.get("/")
                .ifPresentOrElse(
                    (response) -> logger.info("Response: {}", response),
                    () -> logger.error("No response returned from the server")
                );
        } catch (SSLException exception) {
            logger.error("Could not create SSL context", exception);
        }
    }
}
