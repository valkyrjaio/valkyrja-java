/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.uri.enum_;

public enum Scheme {
    EMPTY(""),
    HTTP("http"),
    HTTPS("https");

    private final String value;

    Scheme(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
