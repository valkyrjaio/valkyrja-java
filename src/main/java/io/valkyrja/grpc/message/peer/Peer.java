/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.message.peer;

import io.valkyrja.grpc.message.enum_.AddressType;
import io.valkyrja.grpc.message.peer.contract.AuthContextContract;
import io.valkyrja.grpc.message.peer.contract.PeerContract;

/** Immutable {@link PeerContract} implementation. */
public class Peer implements PeerContract {

    protected final String address;
    protected final AddressType addressType;
    protected final AuthContextContract authContext;

    public Peer(String address, AddressType addressType, AuthContextContract authContext) {
        this.address = address;
        this.addressType = addressType;
        this.authContext = authContext;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public AddressType getAddressType() {
        return addressType;
    }

    @Override
    public AuthContextContract getAuthContext() {
        return authContext;
    }

    /**
     * A peer with an insecure auth context and unknown address type.
     *
     * @param address the peer address
     * @return an insecure peer
     */
    public static Peer insecure(String address) {
        return new Peer(address, AddressType.UNKNOWN, AuthContext.insecure());
    }
}
