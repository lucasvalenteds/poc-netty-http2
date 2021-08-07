package com.example.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http2.DefaultHttp2Connection;
import io.netty.handler.ssl.SslContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public final class Http2ClientInitializer extends ChannelInitializer<SocketChannel> {

    private static final Logger logger = LoggerFactory.getLogger(Http2ClientInitializer.class);

    private final URI server;
    private final SslContext sslContext;
    private final Duration timeout;

    private ChannelPromise promise;

    public Http2ClientInitializer(URI server, SslContext sslContext) {
        this.server = server;
        this.sslContext = sslContext;
        this.timeout = Duration.ofSeconds(1);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Error initializing the client", cause);
        ctx.close();
    }

    @Override
    public void initChannel(SocketChannel channel) {
        this.promise = channel.newPromise();

        channel.pipeline()
            .addLast(sslContext.newHandler(channel.alloc(), server.getHost(), server.getPort()))
            .addLast(new Http2ClientAPN(
                new DefaultHttp2Connection(false),
                new Http2ClientSettingsHandler(promise),
                new Http2ClientResponseHandler(new Http2ClientStreamManager(timeout))
            ));
    }

    public void await() {
        promise.awaitUninterruptibly(timeout.toMillis(), TimeUnit.MILLISECONDS);
    }
}
