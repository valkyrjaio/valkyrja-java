/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.enum_;

public enum ProtocolVersion {
    V1("1.0"),
    V1_1("1.1"),
    V2("2"),
    V3("3");

    private final String value;

    ProtocolVersion(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ProtocolVersion from(String value) {
        for (ProtocolVersion version : values()) {
            if (version.value.equals(value)) {
                return version;
            }
        }
        throw new IllegalArgumentException("Unknown protocol version: " + value);
    }
}
