/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.message.enum_;

/** The transport address family of a connection's peer. */
public enum AddressType {
    IPV4,
    IPV6,
    UNIX,
    UNKNOWN
}
