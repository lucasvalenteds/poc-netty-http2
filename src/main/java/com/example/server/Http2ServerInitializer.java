package com.example.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;

public final class Http2ServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslContext;

    public Http2ServerInitializer(SslContext sslContext) {
        this.sslContext = sslContext;
    }

    @Override
    protected void initChannel(SocketChannel channel) {
        channel.pipeline()
            .addLast(sslContext.newHandler(channel.alloc()), new Http2ServerAPN());
    }
}
