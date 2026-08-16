/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.message.peer;

import io.valkyrja.grpc.message.enum_.AddressType;
import io.valkyrja.grpc.message.peer.contract.AuthContextContract;
import io.valkyrja.grpc.message.peer.contract.PeerContract;

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
