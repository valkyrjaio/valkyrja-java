/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.message.peer.contract;

import io.valkyrja.grpc.message.enum_.AddressType;

/**
 * Information about the connection's other end, derived from the transport rather than a single
 * header. Never null on a service call; its auth context may be {@code "insecure"}.
 */
public interface PeerContract {

    /**
     * Get the peer address, e.g. {@code "192.168.1.5:54321"} or {@code "unix:/var/run/sock"}.
     *
     * @return the address
     */
    String getAddress();

    /**
     * Get the address family of the peer.
     *
     * @return the address type
     */
    AddressType getAddressType();

    /**
     * Get the peer's authentication context. Always present.
     *
     * @return the auth context
     */
    AuthContextContract getAuthContext();
}
