/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.uri.enum_;

public enum Port {

    HTTP(80),
    HTTPS(433);

    private final int value;

    Port(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}