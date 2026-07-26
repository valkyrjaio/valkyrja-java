/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.message.stream;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.jspecify.annotations.Nullable;

/**
 * A live inbound message stream for a streaming-model (bidirectional) call: the transport thread
 * feeds decoded messages with {@link #offer} and signals half-close (or cancellation) with {@link
 * #complete}, while the handler thread drains them by iterating. {@link Iterator#hasNext} blocks
 * until the next message arrives or the stream completes, so a handler can read messages as they
 * arrive without polling.
 *
 * <p>Single-consumer: one handler thread iterates; any number of transport-side threads may feed
 * it. The backing queue is unbounded here — flow control (the {@code maxInboundMessages} high-water
 * mark) is enforced by the adapter, which only requests more from the transport as the handler
 * drains, so the queue never grows past the configured bound in practice.
 */
public final class InboundMessageStream implements Iterable<Object> {

    /** Sentinel enqueued by {@link #complete} to end iteration. */
    private static final Object END = new Object();

    private final BlockingQueue<Object> queue = new LinkedBlockingQueue<>();
    private final Runnable onConsumed;

    public InboundMessageStream() {
        this(() -> {});
    }

    /**
     * @param onConsumed run once each time the handler consumes a message — the adapter wires this
     *     to request one more message from the transport, keeping the queue at its high-water mark.
     */
    public InboundMessageStream(Runnable onConsumed) {
        this.onConsumed = onConsumed;
    }

    /**
     * Feed one decoded message into the stream. Called from the transport thread as messages
     * arrive.
     *
     * @param message the decoded message
     */
    public void offer(Object message) {
        queue.add(message);
    }

    /**
     * Signal that no more messages will arrive — the client half-closed, or the call was cancelled.
     * A blocked {@link Iterator#hasNext} unblocks and ends the iteration.
     */
    public void complete() {
        queue.add(END);
    }

    @Override
    public Iterator<Object> iterator() {
        return new Iterator<>() {
            private @Nullable Object peeked;
            private boolean done;

            @Override
            public boolean hasNext() {
                if (done) {
                    return false;
                }
                if (peeked == null) {
                    Object taken;
                    try {
                        taken = queue.take();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        done = true;
                        return false;
                    }
                    if (taken == END) {
                        done = true;
                        return false;
                    }
                    peeked = taken;
                }
                return true;
            }

            @Override
            public Object next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                Object message = peeked;
                peeked = null;
                onConsumed.run();
                return message;
            }
        };
    }
}
