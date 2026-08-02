/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.message.deadline;

import io.valkyrja.grpc.message.deadline.contract.DeadlineContract;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

/**
 * Immutable {@link DeadlineContract} implementation.
 *
 * <p>Holds an absolute expiry {@link Instant} and consults a {@link Clock} for {@link
 * #getRemaining} and {@link #isExpired}. The system-clock factories ({@link #fromTimeout}, {@link
 * #fromAbsolute}, {@link #none}) cover normal use; the clock-accepting overloads exist for
 * deterministic testing.
 */
public class Deadline implements DeadlineContract {

    /**
     * The sentinel "remaining budget" reported when no deadline is set. A large but finite duration
     * (100 years) so it reads as effectively infinite without overflowing in downstream arithmetic
     * — a consistent choice every language port can reproduce.
     */
    public static final Duration INFINITE_REMAINING = Duration.ofDays(365L * 100);

    protected final Instant absoluteTime;
    protected final boolean hasDeadline;
    protected final Clock clock;

    protected Deadline(Instant absoluteTime, boolean hasDeadline, Clock clock) {
        this.absoluteTime = absoluteTime;
        this.hasDeadline = hasDeadline;
        this.clock = clock;
    }

    @Override
    public Instant getAbsoluteTime() {
        return absoluteTime;
    }

    @Override
    public Duration getRemaining() {
        if (!hasDeadline) {
            return INFINITE_REMAINING;
        }

        Duration remaining = Duration.between(clock.instant(), absoluteTime);

        return remaining.isNegative() ? Duration.ZERO : remaining;
    }

    @Override
    public boolean isExpired() {
        return hasDeadline && !clock.instant().isBefore(absoluteTime);
    }

    @Override
    public boolean hasDeadline() {
        return hasDeadline;
    }

    // --- Factories -------------------------------------------------------------------------------

    public static Deadline fromTimeout(Duration timeout) {
        return fromTimeout(timeout, Clock.systemUTC());
    }

    public static Deadline fromTimeout(Duration timeout, Clock clock) {
        return new Deadline(clock.instant().plus(timeout), true, clock);
    }

    public static Deadline fromAbsolute(Instant absoluteTime) {
        return fromAbsolute(absoluteTime, Clock.systemUTC());
    }

    public static Deadline fromAbsolute(Instant absoluteTime, Clock clock) {
        return new Deadline(absoluteTime, true, clock);
    }

    public static Deadline none() {
        return none(Clock.systemUTC());
    }

    public static Deadline none(Clock clock) {
        return new Deadline(Instant.MAX, false, clock);
    }
}
