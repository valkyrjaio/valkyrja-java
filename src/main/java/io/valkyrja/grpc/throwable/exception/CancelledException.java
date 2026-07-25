/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.throwable.exception;

import io.valkyrja.grpc.message.enum_.CancellationReason;
import io.valkyrja.grpc.throwable.exception.abstract_.GrpcRuntimeException;
import org.jspecify.annotations.Nullable;

/**
 * Thrown when work is performed on a cancelled call.
 *
 * <p>Raised by {@code CancellationToken.throwIfCancelled()} when a handler opts to fail loudly on
 * cancellation. It carries the {@link CancellationReason} so {@code ThrowableCaught} middleware can
 * map it to either {@code CANCELLED} or {@code DEADLINE_EXCEEDED}. Language-native cancellation
 * exceptions are converted to this type at the adapter boundary.
 */
public class CancelledException extends GrpcRuntimeException {

    protected final @Nullable CancellationReason reason;

    public CancelledException(String message) {
        this(message, null);
    }

    public CancelledException(String message, @Nullable CancellationReason reason) {
        super(message);
        this.reason = reason;
    }

    /**
     * Get the cause of the cancellation, if known.
     *
     * @return the reason, or null if unspecified
     */
    public @Nullable CancellationReason getReason() {
        return reason;
    }
}
