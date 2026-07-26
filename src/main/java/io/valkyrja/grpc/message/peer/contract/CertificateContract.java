/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.message.peer.contract;

/**
 * A peer certificate presented during the transport handshake.
 *
 * <p>Modeled agnostically as its encoded (DER) bytes so the framework core stays free of any
 * language- or library-specific certificate type. Adapters translate their native certificate into
 * this shape.
 */
public interface CertificateContract {

    /**
     * Get the encoded (DER) certificate bytes.
     *
     * @return the encoded bytes
     */
    byte[] getEncoded();
}
