/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.enum_;

public enum SameSite {

    NONE("none"),
    LAX("lax"),
    STRICT("strict");

    private final String value;

    SameSite(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}