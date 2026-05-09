/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
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