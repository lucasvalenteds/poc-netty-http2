package com.example.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Http2ServerInitializer extends ChannelInitializer<SocketChannel> {

    private static final Logger logger = LoggerFactory.getLogger(Http2ServerInitializer.class);

    private final SslContext sslContext;

    public Http2ServerInitializer(SslContext sslContext) {
        this.sslContext = sslContext;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Error initializing the server", cause);
        ctx.close();
    }

    @Override
    protected void initChannel(SocketChannel channel) {
        channel.pipeline()
            .addLast(sslContext.newHandler(channel.alloc()), new Http2ServerAPN());
    }
}
