package com.example.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http2.Http2Settings;

public final class Http2ClientSettingsHandler extends SimpleChannelInboundHandler<Http2Settings> {

    private final ChannelPromise promise;

    public Http2ClientSettingsHandler(ChannelPromise promise) {
        this.promise = promise;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Http2Settings msg) {
        promise.setSuccess();

        ctx.pipeline()
            .remove(this);
    }
}