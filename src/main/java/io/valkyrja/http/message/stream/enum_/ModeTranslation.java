/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.stream.enum_;

public enum ModeTranslation {

    NONE(""),
    WINDOWS("t"),
    BINARY_SAFE("b");

    private final String value;

    ModeTranslation(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}