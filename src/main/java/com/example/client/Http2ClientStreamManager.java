package com.example.client;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class Http2ClientStreamManager {

    private static final Map<Integer, Http2ClientStream> STREAMS = new ConcurrentHashMap<>();
    private static final int STREAM_ID = 3;

    private final TimeUnit timeoutUnit;
    private final long timeoutValue;

    public Http2ClientStreamManager(Duration timeout) {
        this.timeoutValue = timeout.toMillis();
        this.timeoutUnit = TimeUnit.MILLISECONDS;
    }

    public Optional<Integer> dispatch(ChannelFuture writeFuture, ChannelPromise promise) {
        STREAMS.put(STREAM_ID, new Http2ClientStream(writeFuture, promise));
        return Optional.of(STREAM_ID);
    }

    public Optional<Http2ClientStream> retrieve(int streamId) {
        return Optional.ofNullable(STREAMS.get(streamId));
    }

    public String await(int streamId) {
        Http2ClientStream stream = this.retrieve(streamId)
            .orElseThrow();

        // Waiting server to write the response
        ChannelFuture writeFuture = stream.getWriteFuture();
        if (!writeFuture.awaitUninterruptibly(timeoutValue, timeoutUnit)) {
            throw new IllegalStateException("Timed out waiting to write for stream " + streamId);
        }
        if (!writeFuture.isSuccess()) {
            throw new RuntimeException(writeFuture.cause());
        }

        // Waiting for server to send the response
        ChannelPromise promise = stream.getPromise();
        if (!promise.awaitUninterruptibly(timeoutValue, timeoutUnit)) {
            throw new IllegalStateException("Timed out waiting for response on stream " + streamId);
        }
        if (!promise.isSuccess()) {
            throw new RuntimeException(promise.cause());
        }

        STREAMS.remove(streamId);

        return stream.getResponse();
    }
}
