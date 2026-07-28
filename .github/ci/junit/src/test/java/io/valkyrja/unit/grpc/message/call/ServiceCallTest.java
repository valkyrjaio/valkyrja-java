/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.grpc.message.call;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.grpc.message.call.ServiceCall;
import io.valkyrja.grpc.message.call.contract.ServiceCallContract;
import io.valkyrja.grpc.message.cancellation.CancellationToken;
import io.valkyrja.grpc.message.deadline.Deadline;
import io.valkyrja.grpc.message.enum_.AddressType;
import io.valkyrja.grpc.message.enum_.CancellationReason;
import io.valkyrja.grpc.message.metadata.Metadata;
import io.valkyrja.grpc.message.metadata.contract.MetadataContract;
import io.valkyrja.grpc.message.peer.Peer;
import io.valkyrja.grpc.routing.data.Route;
import io.valkyrja.grpc.routing.data.contract.RouteContract;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Test the {@link ServiceCall} value type. */
final class ServiceCallTest {

    private static final String METHOD = "/pkg.Greeter/SayHello";

    private static RouteContract route() {
        return new Route(METHOD, (container, r) -> null);
    }

    @Test
    void simpleConstructorDefaults() {
        ServiceCall call = new ServiceCall(METHOD, List.of("m"));
        assertEquals(METHOD, call.getMethod());
        assertFalse(call.getMetadata().has("x"));
        assertFalse(call.getDeadline().hasDeadline());
        assertFalse(call.getCancellation().isCancelled());
        assertEquals(AddressType.UNKNOWN, call.getPeer().getAddressType());
        assertFalse(call.hasRoute());
        assertNull(call.getRoute());
    }

    @Test
    void unaryFactoryCarriesSingleMessage() {
        ServiceCall call = ServiceCall.unary(METHOD, "hello");
        Iterator<Object> it = call.getMessages().iterator();
        assertEquals("hello", it.next());
        assertFalse(it.hasNext());
    }

    @Test
    void aBufferedCallIsNotStreamingAndRejectsSend() {
        ServiceCall call = ServiceCall.unary(METHOD, "hello");
        assertFalse(call.isStreaming());
        assertThrows(IllegalStateException.class, () -> call.send("out"));
    }

    @Test
    void aCallWithASinkIsStreamingAndPushesThroughIt() {
        List<Object> sent = new ArrayList<>();
        ServiceCall call =
                new ServiceCall(
                        METHOD,
                        new Metadata(),
                        Deadline.none(),
                        new CancellationToken(),
                        Peer.insecure("x"),
                        List.of(),
                        null,
                        sent::add);

        assertTrue(call.isStreaming());
        call.send("a");
        call.send("b");
        assertEquals(List.of("a", "b"), sent);
    }

    @Test
    @org.junit.jupiter.api.Timeout(5)
    void concurrentSendIsRejected() throws InterruptedException {
        java.util.concurrent.CountDownLatch insideSink = new java.util.concurrent.CountDownLatch(1);
        java.util.concurrent.CountDownLatch release = new java.util.concurrent.CountDownLatch(1);
        java.util.function.Consumer<Object> blockingSink =
                message -> {
                    insideSink.countDown();
                    try {
                        release.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                };
        ServiceCall call =
                new ServiceCall(
                        METHOD,
                        new Metadata(),
                        Deadline.none(),
                        new CancellationToken(),
                        Peer.insecure("x"),
                        List.of(),
                        null,
                        blockingSink);

        Thread first = new Thread(() -> call.send("a"));
        first.start();
        // The first send is parked inside the sink; a second, concurrent send must be rejected fast
        // rather than racing the non-thread-safe transport.
        assertTrue(insideSink.await(5, java.util.concurrent.TimeUnit.SECONDS));
        assertThrows(IllegalStateException.class, () -> call.send("b"));

        release.countDown();
        first.join();
    }

    @Test
    void withRoutePreservesTheStreamingSink() {
        List<Object> sent = new ArrayList<>();
        ServiceCall call =
                new ServiceCall(
                        METHOD,
                        new Metadata(),
                        Deadline.none(),
                        new CancellationToken(),
                        Peer.insecure("x"),
                        List.of(),
                        null,
                        sent::add);

        ServiceCallContract routed = call.withRoute(route());
        assertTrue(routed.isStreaming());
        routed.send("z");
        assertEquals(List.of("z"), sent);
    }

    @Test
    void fullConstructorExposesEverything() {
        MetadataContract metadata = new Metadata().with("k", "v");
        Deadline deadline = Deadline.fromTimeout(java.time.Duration.ofSeconds(1));
        CancellationToken token = new CancellationToken();
        Peer peer =
                new Peer(
                        "1.2.3.4:5",
                        AddressType.IPV4,
                        io.valkyrja.grpc.message.peer.AuthContext.insecure());
        List<Object> messages = List.of("a");

        ServiceCall call = new ServiceCall(METHOD, metadata, deadline, token, peer, messages, null);
        assertSame(metadata, call.getMetadata());
        assertSame(deadline, call.getDeadline());
        assertSame(token, call.getCancellation());
        assertSame(peer, call.getPeer());
        assertSame(messages, call.getMessages());
    }

    @Test
    void withRouteIsImmutable() {
        ServiceCall call = new ServiceCall(METHOD, List.of("m"));
        RouteContract route = route();
        ServiceCallContract routed = call.withRoute(route);

        assertTrue(routed.hasRoute());
        assertSame(route, routed.getRoute());
        assertFalse(call.hasRoute());
    }

    @Test
    void cancellableYieldsItemsWhenNotCancelled() {
        ServiceCall call = new ServiceCall(METHOD, List.of());
        List<String> collected = new ArrayList<>();
        for (String item : call.cancellable(List.of("a", "b", "c"))) {
            collected.add(item);
        }
        assertEquals(List.of("a", "b", "c"), collected);
    }

    @Test
    void cancellableExitsEarlyWhenCancelledBeforeIteration() {
        CancellationToken token = new CancellationToken();
        token.cancel(CancellationReason.CLIENT_CANCELLED);
        ServiceCall call =
                new ServiceCall(
                        METHOD,
                        new Metadata(),
                        Deadline.none(),
                        token,
                        Peer.insecure("x"),
                        List.of("a"),
                        null);

        // Cancelled before iteration: the drain yields nothing rather than throwing.
        Iterable<String> cancellable = call.cancellable(List.of("a", "b"));
        assertFalseHasNext(cancellable.iterator());
    }

    @Test
    void cancellableStopsMidIterationOnCancel() {
        CancellationToken token = new CancellationToken();
        ServiceCall call =
                new ServiceCall(
                        METHOD,
                        new Metadata(),
                        Deadline.none(),
                        token,
                        Peer.insecure("x"),
                        List.of(),
                        null);

        Iterator<String> it = call.cancellable(List.of("a", "b", "c")).iterator();
        assertTrue(it.hasNext());
        assertEquals("a", it.next());
        token.cancel(CancellationReason.DEADLINE_EXCEEDED);
        // Cancellation mid-drain ends iteration early — no further items, no exception.
        assertFalse(it.hasNext());
    }

    @Test
    void cancellableDrainsFullyWhenNeverCancelled() {
        CancellationToken token = new CancellationToken();
        ServiceCall call =
                new ServiceCall(
                        METHOD,
                        new Metadata(),
                        Deadline.none(),
                        token,
                        Peer.insecure("x"),
                        List.of(),
                        null);

        List<String> collected = new ArrayList<>();
        for (String item : call.cancellable(List.of("a", "b"))) {
            collected.add(item);
        }
        assertEquals(List.of("a", "b"), collected);
    }

    private static void assertFalseHasNext(Iterator<?> it) {
        assertFalse(it.hasNext());
    }

    @Test
    void messagesAreExposed() {
        ServiceCall call = new ServiceCall(METHOD, List.of("m1", "m2"));
        assertNotNull(call.getMessages());
        int count = 0;
        for (Object ignored : call.getMessages()) {
            count++;
        }
        assertEquals(2, count);
    }
}
