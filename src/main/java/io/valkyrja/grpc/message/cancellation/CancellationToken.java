/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.message.cancellation;

import io.valkyrja.grpc.message.cancellation.contract.CancellationTokenContract;
import io.valkyrja.grpc.message.enum_.CancellationReason;
import io.valkyrja.grpc.throwable.exception.CancelledException;
import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.Nullable;

/**
 * Mutable {@link CancellationTokenContract} implementation.
 *
 * <p>Adapters wire the token: they listen to the library's native cancellation signal and to the
 * deadline timer, calling {@link #cancel(CancellationReason)} when either fires. Framework and user
 * code only ever read the token (poll or listener). {@link #never()} is the sentinel used when a
 * call has no cancellation source.
 */
public class CancellationToken implements CancellationTokenContract {

    protected volatile boolean cancelled;
    protected volatile @Nullable CancellationReason reason;
    protected final List<Runnable> listeners = new ArrayList<>();

    public CancellationToken() {
        this(false, null);
    }

    protected CancellationToken(boolean cancelled, @Nullable CancellationReason reason) {
        this.cancelled = cancelled;
        this.reason = reason;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public @Nullable CancellationReason getReason() {
        return reason;
    }

    @Override
    public void throwIfCancelled() {
        if (cancelled) {
            throw new CancelledException("The call has been cancelled", reason);
        }
    }

    @Override
    public void onCancelled(Runnable listener) {
        synchronized (listeners) {
            if (cancelled) {
                listener.run();
                return;
            }

            listeners.add(listener);
        }
    }

    /**
     * Fire cancellation with the given reason. Idempotent: subsequent calls are ignored so the
     * first cause wins and listeners run at most once.
     *
     * @param reason the cause of cancellation
     */
    public void cancel(CancellationReason reason) {
        List<Runnable> toFire;

        synchronized (listeners) {
            if (cancelled) {
                return;
            }

            this.cancelled = true;
            this.reason = reason;
            toFire = new ArrayList<>(listeners);
            listeners.clear();
        }

        for (Runnable listener : toFire) {
            listener.run();
        }
    }

    /**
     * A token that never fires — the sentinel for a call with no cancellation source.
     *
     * @return a never-cancelled token
     */
    public static CancellationToken never() {
        return new CancellationToken(false, null);
    }
}
