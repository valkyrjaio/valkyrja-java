/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.application.entry;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/** A raw-socket HTTP client that returns the full response for the worker-entry smoke tests. */
public final class HttpSmokeClientFixture {

    private HttpSmokeClientFixture() {}

    /**
     * Send {@code GET /?probe=1} to {@code localhost:port} and return the full raw HTTP response.
     *
     * @param port the port to connect to
     * @return the raw HTTP response text
     * @throws IOException if the socket cannot be opened or read
     */
    public static String get(int port) throws IOException {
        try (Socket socket = new Socket("localhost", port)) {
            OutputStream out = socket.getOutputStream();
            out.write(
                    "GET /?probe=1 HTTP/1.1\r\nHost: localhost\r\nConnection: close\r\n\r\n"
                            .getBytes(StandardCharsets.US_ASCII));
            out.flush();

            return new String(socket.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
