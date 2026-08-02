/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.header.value.component.contract;

/**
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7230#section-3.2.6">RFC 7230 Section
 *     3.2.6</a>
 */
public interface ComponentContract {

    static ComponentContract fromValue(String value) {
        throw new UnsupportedOperationException(
                "fromValue must be implemented by the concrete class");
    }

    String getToken();

    ComponentContract withToken(String token);

    String getText();

    ComponentContract withText(String text);

    String jsonSerialize();

    String toString();
}
