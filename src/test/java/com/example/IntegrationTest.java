package com.example;

import com.example.client.Http2Client;
import com.example.server.Http2Server;
import io.netty.channel.ChannelFuture;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntegrationTest {

    private static final URI SERVER_URI = URI.create("https://127.0.0.1:8443");

    private static Http2Server server;
    private static ChannelFuture channel;

    @BeforeAll
    public static void beforeAll() throws Exception {
        server = new Http2Server(SERVER_URI);
        channel = server.start();
    }

    @AfterAll
    public static void afterAll() {
        channel.cancel(true);
        server.close();
    }

    @Test
    public void testReceivingHelloWorldFromTheServer() throws Exception {
        var client = new Http2Client(SERVER_URI);

        var response = client.get("/")
            .orElseThrow();

        assertEquals("Hello World", response);
    }
}
