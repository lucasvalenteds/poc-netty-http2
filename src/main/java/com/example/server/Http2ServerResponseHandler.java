package com.example.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http2.DefaultHttp2DataFrame;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.DefaultHttp2HeadersFrame;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2HeadersFrame;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Http2ServerResponseHandler extends ChannelDuplexHandler {

    private static final Logger logger = LoggerFactory.getLogger(Http2ServerResponseHandler.class);

    private static final ByteBuf RESPONSE_BODY = Unpooled.unreleasableBuffer(
        Unpooled.copiedBuffer("Hello World", CharsetUtil.UTF_8)
    );

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("Error while handling response", cause);
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Http2HeadersFrame) {
            Http2HeadersFrame msgHeader = (Http2HeadersFrame) msg;
            if (msgHeader.isEndStream()) {
                ByteBuf content = ctx.alloc().buffer();
                content.writeBytes(RESPONSE_BODY.duplicate());

                Http2Headers headers = new DefaultHttp2Headers().status(HttpResponseStatus.OK.codeAsText());
                ctx.write(new DefaultHttp2HeadersFrame(headers).stream(msgHeader.stream()));
                ctx.write(new DefaultHttp2DataFrame(content, true).stream(msgHeader.stream()));
            }
        } else {
            super.channelRead(ctx, msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
}
