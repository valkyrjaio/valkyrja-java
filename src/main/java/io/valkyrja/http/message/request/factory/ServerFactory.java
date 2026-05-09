/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.request.factory;

import java.util.Map;

public abstract class ServerFactory {

    public static Map<String, String> normalizeServer(Map<String, String> server) {
        if (server.containsKey("HTTP_AUTHORIZATION")) {
            return server;
        }

        return server;
    }
}