/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.support;

import io.valkyrja.grpc.message.call.contract.ServiceCallContract;
import io.valkyrja.grpc.message.enum_.CancellationReason;
import io.valkyrja.grpc.message.response.ServiceResponse;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import org.jspecify.annotations.Nullable;

/**
 * The shared cancellation check applied at every orchestrator boundary — the "two-question
 * pattern".
 *
 * <p>Asks: (1) has cancellation fired on the call, or the deadline elapsed? (2) does the response
 * in hand already carry a cancellation status? If either is true, a cancellation response is
 * returned to fast-exit up the stack; otherwise {@code null} signals "continue normally".
 *
 * <p>Pre-check (before delegation): a fired cancellation either overlays the existing response's
 * status (preserving accumulated metadata) or, when no response exists yet, builds a fresh one.
 * Post-check (after delegation): a returned cancellation response passes through unchanged.
 */
public final class Cancellation {

    private Cancellation() {}

    /**
     * Run the two-question check.
     *
     * @param call the current call
     * @param response the response in hand, or null if none exists yet
     * @return a cancellation response to fast-exit with, or null to continue normally
     */
    public static @Nullable ServiceResponseContract checkAndFinalize(
            ServiceCallContract call, @Nullable ServiceResponseContract response) {
        if (call.getCancellation().isCancelled()) {
            CancellationReason reason = call.getCancellation().getReason();

            if (response != null) {
                return response.withStatus(ServiceResponse.statusForReason(reason));
            }

            return ServiceResponse.cancelled(reason);
        }

        if (response != null && response.isCancellation()) {
            return response;
        }

        return null;
    }
}
