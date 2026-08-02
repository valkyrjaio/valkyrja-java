/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.message.peer.contract;

import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

/**
 * The authentication context of a connection's peer. Always present; its type may be {@code
 * "insecure"}.
 */
public interface AuthContextContract {

    /**
     * Get the auth type: {@code "ssl"}, {@code "tls"}, {@code "insecure"}, or a custom value.
     *
     * @return the auth type
     */
    String getType();

    /**
     * Get the auth properties as a multi-map of string keys to string values.
     *
     * @return the properties
     */
    Map<String, List<String>> getProperties();

    /**
     * Get the peer certificate chain.
     *
     * @return the certificates; empty if none were presented
     */
    List<CertificateContract> getPeerCertificates();

    /**
     * Get the peer subject (e.g. the certificate subject DN).
     *
     * @return the subject, or null if unknown
     */
    @Nullable String getPeerSubject();

    /**
     * Get the transport security type (e.g. the negotiated cipher/protocol).
     *
     * @return the transport security type, or null if none
     */
    @Nullable String getTransportSecurityType();
}
