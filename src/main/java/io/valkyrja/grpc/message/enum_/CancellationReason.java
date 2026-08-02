/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
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
