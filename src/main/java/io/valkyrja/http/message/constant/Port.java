/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.constant;

public final class Port {

    public static final int MIN = 1;
    public static final int MAX = 65535;
    public static final int HTTP = 80;
    public static final int HTTPS = 443;

    public static boolean isValid(int port) {
        return port >= MIN && port <= MAX;
    }

    private Port() {}
}
