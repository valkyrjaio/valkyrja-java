/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.grpc.message.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.grpc.message.enum_.CancellationReason;
import io.valkyrja.grpc.message.enum_.StatusCode;
import io.valkyrja.grpc.message.metadata.Metadata;
import io.valkyrja.grpc.message.response.ServiceResponse;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.grpc.message.status.Status;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Test the {@link ServiceResponse} value type. */
final class ServiceResponseTest {

    private static int size(Iterable<Object> messages) {
        int count = 0;
        for (Object ignored : messages) {
            count++;
        }
        return count;
    }

    @Test
    void okFactory() {
        ServiceResponse response = ServiceResponse.ok();
        assertTrue(response.getStatus().isOk());
        assertFalse(response.isCancellation());
        assertEquals(0, size(response.getMessages()));
        assertFalse(response.getInitialMetadata().has("x"));
        assertFalse(response.getTrailingMetadata().has("x"));
    }

    @Test
    void okWithMessage() {
        Object message = new Object();
        ServiceResponse response = ServiceResponse.ok(message);
        assertEquals(1, size(response.getMessages()));
        assertEquals(message, response.getMessages().iterator().next());
    }

    @Test
    void unimplementedFactories() {
        assertEquals(
                StatusCode.UNIMPLEMENTED, ServiceResponse.unimplemented().getStatus().getCode());
        assertEquals("nope", ServiceResponse.unimplemented("nope").getStatus().getMessage());
    }

    @Test
    void cancelledMapsReasonToStatus() {
        assertEquals(
                StatusCode.DEADLINE_EXCEEDED,
                ServiceResponse.cancelled(CancellationReason.DEADLINE_EXCEEDED)
                        .getStatus()
                        .getCode());
        assertEquals(
                StatusCode.CANCELLED,
                ServiceResponse.cancelled(CancellationReason.CLIENT_CANCELLED)
                        .getStatus()
                        .getCode());
        assertEquals(StatusCode.CANCELLED, ServiceResponse.cancelled(null).getStatus().getCode());
    }

    @Test
    void statusForReason() {
        assertEquals(
                StatusCode.DEADLINE_EXCEEDED,
                ServiceResponse.statusForReason(CancellationReason.DEADLINE_EXCEEDED).getCode());
        assertEquals(
                StatusCode.CANCELLED,
                ServiceResponse.statusForReason(CancellationReason.CLIENT_CANCELLED).getCode());
    }

    @Test
    void withStatusIsImmutable() {
        ServiceResponse base = ServiceResponse.ok();
        ServiceResponseContract updated = base.withStatus(Status.notFound(null));
        assertEquals(StatusCode.NOT_FOUND, updated.getStatus().getCode());
        assertTrue(base.getStatus().isOk());
    }

    @Test
    void withInitialMetadata() {
        ServiceResponseContract response =
                ServiceResponse.ok().withInitialMetadata(new Metadata().with("k", "v"));
        assertEquals("v", response.getInitialMetadata().get("k"));
    }

    @Test
    void withTrailingMetadata() {
        ServiceResponseContract response =
                ServiceResponse.ok().withTrailingMetadata(new Metadata().with("k", "v"));
        assertEquals("v", response.getTrailingMetadata().get("k"));
    }

    @Test
    void withMessages() {
        ServiceResponseContract response = ServiceResponse.ok().withMessages(List.of("a", "b"));
        assertEquals(2, size(response.getMessages()));
    }

    @Test
    void ofBuildsFromStatus() {
        ServiceResponse response = ServiceResponse.of(Status.internal(null));
        assertEquals(StatusCode.INTERNAL, response.getStatus().getCode());
    }
}
