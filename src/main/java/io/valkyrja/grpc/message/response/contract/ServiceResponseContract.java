/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.message.response.contract;

import io.valkyrja.grpc.message.metadata.contract.MetadataContract;
import io.valkyrja.grpc.message.status.contract.StatusContract;

/**
 * The immutable outbound side of the wire: what the handler and pipeline produce and the adapter
 * flushes to the client.
 *
 * <p>Messages are typed agnostically as {@link Object}: unary responses use a single-element
 * iterable, streaming responses use a lazy iterable. The underlying concrete message type is
 * per-application (the generated protobuf type) and never referenced by the framework.
 *
 * <p>Initial metadata locks the moment the first message is written to the wire; trailing metadata
 * stays mutable until the handler returns and the adapter flushes the call's close.
 */
public interface ServiceResponseContract {

    /**
     * Get the call outcome.
     *
     * @return the status
     */
    StatusContract getStatus();

    /**
     * Return a copy with the given status.
     *
     * @param status the new status
     * @return a new response
     */
    ServiceResponseContract withStatus(StatusContract status);

    /**
     * Get the initial response metadata (leading HTTP/2 headers).
     *
     * @return the initial metadata
     */
    MetadataContract getInitialMetadata();

    /**
     * Return a copy with the given initial metadata.
     *
     * @param metadata the new initial metadata
     * @return a new response
     */
    ServiceResponseContract withInitialMetadata(MetadataContract metadata);

    /**
     * Get the trailing response metadata (HTTP/2 trailing headers).
     *
     * @return the trailing metadata
     */
    MetadataContract getTrailingMetadata();

    /**
     * Return a copy with the given trailing metadata.
     *
     * @param metadata the new trailing metadata
     * @return a new response
     */
    ServiceResponseContract withTrailingMetadata(MetadataContract metadata);

    /**
     * Get the outbound messages.
     *
     * @return the messages
     */
    Iterable<Object> getMessages();

    /**
     * Return a copy with the given outbound messages.
     *
     * @param messages the new messages
     * @return a new response
     */
    ServiceResponseContract withMessages(Iterable<Object> messages);

    /**
     * Whether the status is a cancellation outcome.
     *
     * @return true for {@code CANCELLED} or {@code DEADLINE_EXCEEDED}
     */
    boolean isCancellation();
}
