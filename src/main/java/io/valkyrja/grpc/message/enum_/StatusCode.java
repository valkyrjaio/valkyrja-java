/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.message.enum_;

import io.valkyrja.grpc.throwable.exception.GrpcInvalidStatusCodeException;

/**
 * The canonical gRPC status codes.
 *
 * <p>These are gRPC-specific and intentionally distinct from HTTP status codes: the ranges, names,
 * and semantics differ, and reusing HTTP's would accept values with no meaning on the wire. The
 * numeric value is the integer carried in the {@code grpc-status} trailer (0–16).
 *
 * @see <a href="https://grpc.github.io/grpc/core/md_doc_statuscodes.html">gRPC Status Codes</a>
 */
public enum StatusCode {
    OK(0, "OK"),
    CANCELLED(1, "The operation was cancelled"),
    UNKNOWN(2, "Unknown error"),
    INVALID_ARGUMENT(3, "Invalid argument"),
    DEADLINE_EXCEEDED(4, "Deadline exceeded"),
    NOT_FOUND(5, "Not found"),
    ALREADY_EXISTS(6, "Already exists"),
    PERMISSION_DENIED(7, "Permission denied"),
    RESOURCE_EXHAUSTED(8, "Resource exhausted"),
    FAILED_PRECONDITION(9, "Failed precondition"),
    ABORTED(10, "Aborted"),
    OUT_OF_RANGE(11, "Out of range"),
    UNIMPLEMENTED(12, "Unimplemented"),
    INTERNAL(13, "Internal error"),
    UNAVAILABLE(14, "Unavailable"),
    DATA_LOSS(15, "Data loss"),
    UNAUTHENTICATED(16, "Unauthenticated");

    private final int value;
    private final String defaultMessage;

    StatusCode(int value, String defaultMessage) {
        this.value = value;
        this.defaultMessage = defaultMessage;
    }

    /**
     * Get the integer value carried in the {@code grpc-status} trailer.
     *
     * @return the status code value (0–16)
     */
    public int getValue() {
        return value;
    }

    /**
     * Get the default human-readable message for this code.
     *
     * @return the default message
     */
    public String getDefaultMessage() {
        return defaultMessage;
    }

    /**
     * Whether this code represents a successful call outcome.
     *
     * @return true if this is {@link #OK}
     */
    public boolean isOk() {
        return this == OK;
    }

    /**
     * Whether this code represents a cancellation outcome.
     *
     * @return true for {@link #CANCELLED} or {@link #DEADLINE_EXCEEDED}
     */
    public boolean isCancellation() {
        return this == CANCELLED || this == DEADLINE_EXCEEDED;
    }

    /**
     * Resolve a status code from its integer wire value.
     *
     * @param value the integer value (0–16)
     * @return the matching status code
     * @throws GrpcInvalidStatusCodeException if no code matches the value
     */
    public static StatusCode fromValue(int value) {
        for (StatusCode code : values()) {
            if (code.value == value) {
                return code;
            }
        }

        throw new GrpcInvalidStatusCodeException("No gRPC status code for value `" + value + "`");
    }
}
