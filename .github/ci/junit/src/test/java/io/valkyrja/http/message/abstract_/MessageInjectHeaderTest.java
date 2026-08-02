/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.abstract_;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.valkyrja.http.message.header.Header;
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.header.collection.contract.HeaderCollectionContract;
import io.valkyrja.http.message.response.Response;
import org.junit.jupiter.api.Test;

/**
 * Package-private test for the protected {@link Message#injectHeader} helper, which has no callers
 * in the public API. Placed in the source package per the project's package-private convention.
 */
final class MessageInjectHeaderTest {

    private final Message message = new Response();

    @Test
    void overrideReplacesExistingHeader() {
        HeaderCollectionContract headers = new HeaderCollection(new Header("X-Test", "old"));

        var result = message.injectHeader(new Header("X-Test", "new"), headers, true);

        assertEquals("new", result.getHeaderLine("x-test"));
    }

    @Test
    void nonOverrideMergesIntoExistingHeader() {
        HeaderCollectionContract headers = new HeaderCollection(new Header("X-Test", "a"));

        var result = message.injectHeader(new Header("X-Test", "b"), headers, false);

        assertEquals(2, result.get("x-test").getValues().size());
    }

    @Test
    void addsHeaderWhenAbsent() {
        var result = message.injectHeader(new Header("X-New", "v"), new HeaderCollection(), false);

        assertEquals("v", result.getHeaderLine("x-new"));
    }
}
