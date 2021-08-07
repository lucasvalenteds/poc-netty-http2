package com.example.server;

import com.example.util.Certificates;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import javax.net.ssl.SSLException;
import java.net.URI;
import java.security.cert.CertificateException;

public final class Http2Server implements AutoCloseable {

    private final Channel channel;

    public Http2Server(URI uri) throws CertificateException, SSLException, InterruptedException {
        this.channel = new ServerBootstrap()
            .option(ChannelOption.SO_BACKLOG, 1024)
            .group(new NioEventLoopGroup())
            .channel(NioServerSocketChannel.class)
            .childHandler(new Http2ServerInitializer(Certificates.createSelfSignedServerCertificate()))
            .bind(uri.getPort())
            .sync()
            .channel();
    }

    public ChannelFuture start() {
        return channel.closeFuture();
    }

    public void startAndBlock() throws InterruptedException {
        channel.closeFuture()
            .sync();
    }

    @Override
    public void close() {
        channel.eventLoop()
            .parent()
            .shutdownGracefully();
    }
}
