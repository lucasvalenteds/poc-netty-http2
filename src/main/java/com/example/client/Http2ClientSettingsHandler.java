package com.example.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http2.Http2Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Http2ClientSettingsHandler extends SimpleChannelInboundHandler<Http2Settings> {

    private static final Logger logger = LoggerFactory.getLogger(Http2ClientSettingsHandler.class);

    private final ChannelPromise promise;

    public Http2ClientSettingsHandler(ChannelPromise promise) {
        this.promise = promise;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Error while handling settings response", cause);
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Http2Settings msg) {
        promise.setSuccess();

        ctx.pipeline()
            .remove(this);
    }
}