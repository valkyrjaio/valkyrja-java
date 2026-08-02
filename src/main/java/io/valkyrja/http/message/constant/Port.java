/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
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
