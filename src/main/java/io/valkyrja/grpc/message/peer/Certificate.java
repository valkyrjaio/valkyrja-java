/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
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
