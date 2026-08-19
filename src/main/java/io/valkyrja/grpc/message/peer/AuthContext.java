/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.message.peer;

import io.valkyrja.grpc.message.peer.contract.AuthContextContract;
import io.valkyrja.grpc.message.peer.contract.CertificateContract;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

public class AuthContext implements AuthContextContract {

    public static final String TYPE_INSECURE = "insecure";

    protected final String type;
    protected final Map<String, List<String>> properties;
    protected final List<CertificateContract> peerCertificates;
    protected final @Nullable String peerSubject;
    protected final @Nullable String transportSecurityType;

    public AuthContext(String type) {
        this(type, Map.of(), List.of(), null, null);
    }

    public AuthContext(
            String type,
            Map<String, List<String>> properties,
            List<CertificateContract> peerCertificates,
            @Nullable String peerSubject,
            @Nullable String transportSecurityType) {
        Map<String, List<String>> propsCopy = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : properties.entrySet()) {
            propsCopy.put(entry.getKey(), List.copyOf(entry.getValue()));
        }

        this.type = type;
        this.properties = Collections.unmodifiableMap(propsCopy);
        this.peerCertificates = List.copyOf(peerCertificates);
        this.peerSubject = peerSubject;
        this.transportSecurityType = transportSecurityType;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Map<String, List<String>> getProperties() {
        return properties;
    }

    @Override
    public List<CertificateContract> getPeerCertificates() {
        return new ArrayList<>(peerCertificates);
    }

    @Override
    public @Nullable String getPeerSubject() {
        return peerSubject;
    }

    @Override
    public @Nullable String getTransportSecurityType() {
        return transportSecurityType;
    }

    /**
     * An auth context for an insecure (plaintext) connection.
     *
     * @return an insecure auth context
     */
    public static AuthContext insecure() {
        return new AuthContext(TYPE_INSECURE);
    }
}
