package com.example;

import com.example.server.Http2Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.net.URI;
import java.security.cert.CertificateException;

public final class ServerExample {

    private static final Logger logger = LoggerFactory.getLogger(Http2Server.class);

    public static void main(String[] args) {
        var uri = URI.create("https://127.0.0.1:8443/");

        try (var server = new Http2Server(uri)) {
            logger.info("HTTP/2 server is listening on {}", uri);

            server.startAndBlock();
        } catch (CertificateException exception) {
            logger.error("Could not create self signed certificate", exception);
        } catch (SSLException exception) {
            logger.error("Could not create SSL context", exception);
        } catch (InterruptedException exception) {
            logger.error("Could not start HTTP/2 server", exception);
        }
    }
}
