/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.message.cancellation.contract;

import io.valkyrja.grpc.message.enum_.CancellationReason;
import org.jspecify.annotations.Nullable;

/**
 * The signal for "should this work stop?"
 *
 * <p>Unifies two causes: client-initiated cancellation (HTTP/2 RST_STREAM) and deadline expiry.
 * Deadline expiry is modeled as a cause of cancellation; code only checks cancellation, consulting
 * {@link #getReason()} if the distinction matters. The base contract is poll + listener, which
 * works in every language; language-native awaitable extensions may be layered on per port.
 */
public interface CancellationTokenContract {

    /**
     * Whether cancellation has fired.
     *
     * @return true if cancelled
     */
    boolean isCancelled();

    /**
     * Get the cause of cancellation.
     *
     * @return the reason, or null if not cancelled
     */
    @Nullable CancellationReason getReason();

    /** Throw a {@code CancelledException} if the call is cancelled; otherwise do nothing. */
    void throwIfCancelled();

    /**
     * Register a listener fired when cancellation occurs. If already cancelled, the listener runs
     * immediately.
     *
     * @param listener the callback to run on cancellation
     */
    void onCancelled(Runnable listener);
}
