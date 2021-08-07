package com.example.client;

import com.example.util.Certificates;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpScheme;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http2.HttpConversionUtil;

import javax.net.ssl.SSLException;
import java.net.URI;
import java.time.Duration;
import java.util.Optional;

public final class Http2Client implements AutoCloseable {

    private final URI server;
    private final Channel channel;
    private final Http2ClientStreamManager streamManager;
    private final Http2ClientInitializer initializer;

    public Http2Client(URI server) throws SSLException {
        this.server = server;
        this.streamManager = new Http2ClientStreamManager(Duration.ofSeconds(5));
        this.initializer = new Http2ClientInitializer(server, Certificates.createSelfSignedClientCertificate());
        this.channel = new Bootstrap()
            .group(new NioEventLoopGroup())
            .channel(NioSocketChannel.class)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .remoteAddress(server.getHost(), server.getPort())
            .handler(initializer)
            .connect()
            .syncUninterruptibly()
            .channel();
    }

    public Optional<String> get(String path) {
        initializer.await();

        FullHttpRequest request = new DefaultFullHttpRequest(
            HttpVersion.valueOf("HTTP/2.0"),
            HttpMethod.GET,
            path,
            Unpooled.EMPTY_BUFFER
        );

        request.headers()
            .add(HttpHeaderNames.HOST, server.getHost())
            .add(HttpConversionUtil.ExtensionHeaderNames.SCHEME.text(), HttpScheme.HTTPS)
            .add(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP)
            .add(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.DEFLATE);

        return streamManager.dispatch(channel.writeAndFlush(request), channel.newPromise())
            .map(streamManager::await);
    }

    @Override
    public void close() {
        channel.eventLoop()
            .parent()
            .shutdownGracefully();
    }
}
