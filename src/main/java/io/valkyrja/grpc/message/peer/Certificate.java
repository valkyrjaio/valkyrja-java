/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.message.peer;

import io.valkyrja.grpc.message.peer.contract.CertificateContract;

/** Immutable {@link CertificateContract} implementation wrapping encoded (DER) bytes. */
public class Certificate implements CertificateContract {

    protected final byte[] encoded;

    public Certificate(byte[] encoded) {
        this.encoded = encoded.clone();
    }

    @Override
    public byte[] getEncoded() {
        return encoded.clone();
    }
}
