/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
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
