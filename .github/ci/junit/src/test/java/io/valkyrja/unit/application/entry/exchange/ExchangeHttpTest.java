/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.application.entry.exchange;

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
