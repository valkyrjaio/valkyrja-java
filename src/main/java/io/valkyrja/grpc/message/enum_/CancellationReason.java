/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.message.enum_;

/**
 * The cause behind a cancelled call.
 *
 * <p>Cancellation unifies two causes: client-initiated cancellation (HTTP/2 RST_STREAM) and
 * deadline expiry. Code only checks cancellation; it consults the reason when the distinction
 * matters.
 */
public enum CancellationReason {
    CLIENT_CANCELLED,
    DEADLINE_EXCEEDED
}
