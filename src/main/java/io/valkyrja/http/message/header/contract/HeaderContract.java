/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.header.contract;

import java.util.List;

/**
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7230#section-3.2">RFC 7230 Section 3.2</a>
 */
public interface HeaderContract {

    static HeaderContract fromValue(String value) {
        throw new UnsupportedOperationException(
                "fromValue must be implemented by the concrete class");
    }

    String getName();

    String getNormalizedName();

    HeaderContract withName(String name);

    List<Object> getValues();

    HeaderContract withValues(Object... values);

    HeaderContract withAddedValues(Object... values);

    String getHeaderLine();

    String jsonSerialize();

    String toString();

    // Iterator methods
    Object current();

    int key();

    void next();

    void rewind();

    boolean valid();

    // Countable
    int count();

    // ArrayAccess
    boolean offsetExists(int offset);

    Object offsetGet(int offset);

    void offsetSet(int offset, Object value);

    void offsetUnset(int offset);
}
