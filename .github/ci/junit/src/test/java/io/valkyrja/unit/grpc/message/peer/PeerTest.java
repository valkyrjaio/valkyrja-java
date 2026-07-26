/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.grpc.message.peer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.grpc.message.enum_.AddressType;
import io.valkyrja.grpc.message.peer.AuthContext;
import io.valkyrja.grpc.message.peer.Certificate;
import io.valkyrja.grpc.message.peer.Peer;
import io.valkyrja.grpc.message.peer.contract.AuthContextContract;
import io.valkyrja.grpc.message.peer.contract.CertificateContract;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Test {@link Peer}, {@link AuthContext}, and {@link Certificate}. */
final class PeerTest {

    @Test
    void peerExposesAddressAndAuth() {
        AuthContextContract auth = AuthContext.insecure();
        Peer peer = new Peer("192.168.1.5:54321", AddressType.IPV4, auth);
        assertEquals("192.168.1.5:54321", peer.getAddress());
        assertEquals(AddressType.IPV4, peer.getAddressType());
        assertEquals(auth, peer.getAuthContext());
    }

    @Test
    void insecurePeerFactory() {
        Peer peer = Peer.insecure("unix:/var/run/sock");
        assertEquals("unix:/var/run/sock", peer.getAddress());
        assertEquals(AddressType.UNKNOWN, peer.getAddressType());
        assertEquals(AuthContext.TYPE_INSECURE, peer.getAuthContext().getType());
    }

    @Test
    void insecureAuthContextDefaults() {
        AuthContext auth = AuthContext.insecure();
        assertEquals("insecure", auth.getType());
        assertTrue(auth.getProperties().isEmpty());
        assertTrue(auth.getPeerCertificates().isEmpty());
        assertNull(auth.getPeerSubject());
        assertNull(auth.getTransportSecurityType());
    }

    @Test
    void fullAuthContext() {
        CertificateContract cert = new Certificate(new byte[] {1, 2, 3});
        AuthContext auth =
                new AuthContext(
                        "tls",
                        Map.of("cipher", List.of("AES")),
                        List.of(cert),
                        "CN=example",
                        "TLSv1.3");
        assertEquals("tls", auth.getType());
        assertEquals(List.of("AES"), auth.getProperties().get("cipher"));
        assertEquals(1, auth.getPeerCertificates().size());
        assertEquals("CN=example", auth.getPeerSubject());
        assertEquals("TLSv1.3", auth.getTransportSecurityType());
    }

    @Test
    void authContextPropertiesAreUnmodifiable() {
        AuthContext auth = new AuthContext("tls", Map.of("a", List.of("b")), List.of(), null, null);
        assertThrows(
                UnsupportedOperationException.class, () -> auth.getProperties().put("c", List.of()));
    }

    @Test
    void certificateEncodedIsDefensivelyCopied() {
        byte[] source = {1, 2, 3};
        Certificate cert = new Certificate(source);
        source[0] = 0;
        assertArrayEquals(new byte[] {1, 2, 3}, cert.getEncoded());

        byte[] returned = cert.getEncoded();
        returned[0] = 0;
        assertArrayEquals(new byte[] {1, 2, 3}, cert.getEncoded());
    }
}
