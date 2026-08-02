/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.application.entry.exchange;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.valkyrja.application.entry.exchange.ExchangeCgiHttp;
import io.valkyrja.application.entry.exchange.ExchangeHttp;
import org.junit.jupiter.api.Test;

/** Test the {@link ExchangeHttp} and {@link ExchangeCgiHttp} entry points. */
final class ExchangeHttpTest {

    @Test
    void exchangeHttpIsInstantiable() {
        assertNotNull(new ExchangeHttp());
    }

    @Test
    void exchangeCgiHttpIsInstantiable() {
        assertNotNull(new ExchangeCgiHttp());
    }
}
