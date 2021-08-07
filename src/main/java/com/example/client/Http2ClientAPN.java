package com.example.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.DelegatingDecompressorFrameListener;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.HttpToHttp2ConnectionHandler;
import io.netty.handler.codec.http2.HttpToHttp2ConnectionHandlerBuilder;
import io.netty.handler.codec.http2.InboundHttp2ToHttpAdapterBuilder;
import io.netty.handler.ssl.ApplicationProtocolNames;
import io.netty.handler.ssl.ApplicationProtocolNegotiationHandler;

public final class Http2ClientAPN extends ApplicationProtocolNegotiationHandler {

    private final Http2ClientSettingsHandler settingsHandler;
    private final Http2ClientResponseHandler responseHandler;
    private final HttpToHttp2ConnectionHandler connectionHandler;

    public Http2ClientAPN(Http2Connection connection,
                          Http2ClientSettingsHandler settingsHandler,
                          Http2ClientResponseHandler responseHandler) {
        super(ApplicationProtocolNames.HTTP_2);
        this.settingsHandler = settingsHandler;
        this.responseHandler = responseHandler;
        this.connectionHandler = new HttpToHttp2ConnectionHandlerBuilder()
            .frameListener(
                new DelegatingDecompressorFrameListener(
                    connection,
                    new InboundHttp2ToHttpAdapterBuilder(connection)
                        .maxContentLength(Integer.MAX_VALUE)
                        .propagateSettings(true)
                        .build()
                )
            )
            .connection(connection)
            .build();
    }

    @Override
    protected void configurePipeline(ChannelHandlerContext ctx, String protocol) {
        if (!ApplicationProtocolNames.HTTP_2.equals(protocol)) {
            ctx.close();
            throw new IllegalStateException("Protocol: " + protocol + " not supported");
        }

        ctx.pipeline()
            .addLast(connectionHandler)
            .addLast(settingsHandler, responseHandler);
    }
}
