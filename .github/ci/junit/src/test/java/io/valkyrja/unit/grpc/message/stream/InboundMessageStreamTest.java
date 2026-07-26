/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.grpc.message.stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import io.valkyrja.grpc.message.stream.InboundMessageStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/** Test the {@link InboundMessageStream} live inbound queue. */
final class InboundMessageStreamTest {

    @Test
    void drainsOfferedMessagesInOrderThenEndsOnComplete() {
        InboundMessageStream stream = new InboundMessageStream();
        stream.offer("a");
        stream.offer("b");
        stream.complete();

        List<Object> collected = new ArrayList<>();
        for (Object message : stream) {
            collected.add(message);
        }
        assertEquals(List.of("a", "b"), collected);
    }

    @Test
    void completeWithNoMessagesYieldsAnEmptyIteration() {
        InboundMessageStream stream = new InboundMessageStream();
        stream.complete();

        Iterator<Object> it = stream.iterator();
        assertFalse(it.hasNext());
        // A second call short-circuits on the completed flag rather than blocking again.
        assertFalse(it.hasNext());
    }

    @Test
    void hasNextIsIdempotentAndDoesNotConsume() {
        InboundMessageStream stream = new InboundMessageStream();
        stream.offer("only");
        stream.complete();

        Iterator<Object> it = stream.iterator();
        assertTrue(it.hasNext());
        assertTrue(it.hasNext());
        assertEquals("only", it.next());
        assertFalse(it.hasNext());
    }

    @Test
    void firesTheConsumeCallbackOncePerDrainedMessageOnly() {
        java.util.concurrent.atomic.AtomicInteger consumed =
                new java.util.concurrent.atomic.AtomicInteger();
        InboundMessageStream stream = new InboundMessageStream(consumed::incrementAndGet);
        stream.offer("a");
        stream.offer("b");
        stream.complete();

        for (Object ignored : stream) {
            // drain
        }
        // Fired once per real message, not for the terminal sentinel.
        assertEquals(2, consumed.get());
    }

    @Test
    void nextThrowsWhenExhausted() {
        InboundMessageStream stream = new InboundMessageStream();
        stream.complete();

        assertThrows(NoSuchElementException.class, () -> stream.iterator().next());
    }

    @Test
    void hasNextReturnsFalseWhenTheConsumerIsInterrupted() {
        InboundMessageStream stream = new InboundMessageStream();
        Iterator<Object> it = stream.iterator();

        Thread.currentThread().interrupt();
        // take() throws InterruptedException immediately when the thread is already interrupted.
        assertFalse(it.hasNext());
        assertTrue(Thread.interrupted()); // clear the flag for subsequent tests
    }

    @Test
    @Timeout(5)
    void hasNextReturnsFalseWhenTheConsumerIsInterruptedWhileParkedInTake()
            throws InterruptedException {
        InboundMessageStream stream = new InboundMessageStream();
        AtomicBoolean iterationEnded = new AtomicBoolean();

        Thread consumer =
                new Thread(
                        () -> {
                            for (Object ignored : stream) {
                                // never reached — nothing is ever offered
                            }
                            iterationEnded.set(true);
                        });
        consumer.start();

        // Interrupt only once the consumer is genuinely blocked inside take(), not before.
        awaitParkedInTake(consumer);
        consumer.interrupt();
        consumer.join();

        assertTrue(iterationEnded.get(), "iteration did not end after the parked thread was interrupted");
    }

    @Test
    @Timeout(5)
    void hasNextBlocksUntilAMessageArrivesFromAnotherThread() throws InterruptedException {
        InboundMessageStream stream = new InboundMessageStream();
        List<Object> collected = new ArrayList<>();

        Thread consumer =
                new Thread(
                        () -> {
                            for (Object message : stream) {
                                collected.add(message);
                            }
                        });
        consumer.start();

        // Only feed once the consumer is provably parked in take(), so this exercises the real
        // blocked -> woken transition rather than degrading to offer-then-take.
        awaitParkedInTake(consumer);
        stream.offer("x");
        stream.offer("y");
        stream.complete();

        consumer.join();
        assertEquals(List.of("x", "y"), collected);
    }

    /** Spin until the thread is parked waiting (blocked inside {@code queue.take()}). */
    private static void awaitParkedInTake(Thread thread) throws InterruptedException {
        for (int i = 0; i < 2000; i++) {
            Thread.State state = thread.getState();
            if (state == Thread.State.WAITING || state == Thread.State.TIMED_WAITING) {
                return;
            }
            Thread.sleep(1);
        }
        fail("consumer never parked in take()");
    }
}
