package com.example.client;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;

public final class Http2ClientStream {

    private final ChannelFuture writeFuture;
    private final ChannelPromise promise;

    private String response;

    public Http2ClientStream(ChannelFuture writeFuture, ChannelPromise promise) {
        this.writeFuture = writeFuture;
        this.promise = promise;
    }

    public ChannelFuture getWriteFuture() {
        return writeFuture;
    }

    public ChannelPromise getPromise() {
        return promise;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
