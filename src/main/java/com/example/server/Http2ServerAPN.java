package com.example.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.Http2FrameCodecBuilder;
import io.netty.handler.ssl.ApplicationProtocolNames;
import io.netty.handler.ssl.ApplicationProtocolNegotiationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Http2ServerAPN extends ApplicationProtocolNegotiationHandler {

    private static final Logger logger = LoggerFactory.getLogger(Http2ServerAPN.class);

    public Http2ServerAPN() {
        super(ApplicationProtocolNames.HTTP_2);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Error configuring server APN", cause);
        ctx.close();
    }

    @Override
    protected void configurePipeline(ChannelHandlerContext ctx, String protocol) {
        if (!ApplicationProtocolNames.HTTP_2.equals(protocol)) {
            throw new IllegalStateException("Protocol: " + protocol + " not supported");
        }

        ctx.pipeline()
            .addLast(Http2FrameCodecBuilder.forServer().build(), new Http2ServerResponseHandler());
    }
}
