/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.message.status.contract;

import io.valkyrja.grpc.message.enum_.StatusCode;
import org.jspecify.annotations.Nullable;

/**
 * The immutable outcome of a gRPC call: a code, a human-readable message, and optional rich error
 * details.
 *
 * <p>Mirrors the pattern HTTP uses for status code plus reason phrase, with an additional field for
 * {@code google.rpc.Status} protobuf bytes carried in the {@code grpc-status-details-bin} trailer.
 */
public interface StatusContract {

    /**
     * Get the gRPC status code.
     *
     * @return the status code
     */
    StatusCode getCode();

    /**
     * Get the human-readable message. Never null; defaults from the code.
     *
     * @return the message
     */
    String getMessage();

    /**
     * Get the optional rich error details ({@code google.rpc.Status} protobuf bytes).
     *
     * @return the details, or null if none
     */
    byte @Nullable [] getDetails();

    /**
     * Whether details are present.
     *
     * @return true if details are set
     */
    boolean hasDetails();

    /**
     * Whether the call succeeded.
     *
     * @return true if the code is {@link StatusCode#OK}
     */
    boolean isOk();

    /**
     * Whether the call was cancelled or its deadline elapsed.
     *
     * @return true for {@code CANCELLED} or {@code DEADLINE_EXCEEDED}
     */
    boolean isCancellation();

    /**
     * Return a copy with the given code.
     *
     * @param code the new code
     * @return a new status
     */
    StatusContract withCode(StatusCode code);

    /**
     * Return a copy with the given message.
     *
     * @param message the new message
     * @return a new status
     */
    StatusContract withMessage(String message);

    /**
     * Return a copy with the given rich error details.
     *
     * @param details the {@code google.rpc.Status} protobuf bytes
     * @return a new status
     */
    StatusContract withDetails(byte @Nullable [] details);
}
