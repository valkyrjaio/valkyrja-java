/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.message.deadline.contract;

import java.time.Duration;
import java.time.Instant;

/**
 * The absolute time at which a call's budget expires.
 *
 * <p>Computed once at call receipt from the inbound {@code grpc-timeout} header and propagated as
 * an absolute time so every downstream layer agrees on the same reference point. Never null on a
 * service call; {@code none()} is the sentinel for "no deadline set by the client."
 */
public interface DeadlineContract {

    /**
     * Get the absolute time at which the budget expires.
     *
     * @return the absolute expiry time; {@link Instant#MAX} when no deadline is set
     */
    Instant getAbsoluteTime();

    /**
     * Get the remaining budget from now.
     *
     * @return the remaining duration; {@link Duration#ZERO} if already expired, and a very large
     *     duration when no deadline is set
     */
    Duration getRemaining();

    /**
     * Whether the deadline has elapsed.
     *
     * @return true if expired; always false when no deadline is set
     */
    boolean isExpired();

    /**
     * Whether a deadline is set at all.
     *
     * @return true if a deadline is set
     */
    boolean hasDeadline();
}
