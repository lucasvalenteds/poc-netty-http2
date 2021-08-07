package com.example.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http2.HttpConversionUtil;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public final class Http2ClientResponseHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    private static final Logger logger = LoggerFactory.getLogger(Http2ClientResponseHandler.class);

    private final Http2ClientStreamManager requestManager;

    public Http2ClientResponseHandler(Http2ClientStreamManager requestManager) {
        this.requestManager = requestManager;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Error while handling response", cause);
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) {
        var streamId = msg.headers()
            .getInt(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text());

        Optional<Http2ClientStream> streamOptional = requestManager.retrieve(streamId);
        if (streamOptional.isEmpty()) {
            ctx.close();
            return;
        }

        var stream = streamOptional.get();
        var content = msg.content();
        if (content.isReadable()) {
            var contentLength = content.readableBytes();

            var array = new byte[contentLength];
            content.readBytes(array);

            var response = new String(array, 0, contentLength, CharsetUtil.UTF_8);
            stream.setResponse(response);
        }

        stream.getPromise()
            .setSuccess();
    }
}