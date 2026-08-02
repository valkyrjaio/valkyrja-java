/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.message.enum_;

/** The transport address family of a connection's peer. */
public enum AddressType {
    IPV4,
    IPV6,
    UNIX,
    UNKNOWN
}
