/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.routing.collector;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.routing.data.Parameter;
import io.valkyrja.type.data.Cast;
import org.junit.jupiter.api.Test;

/** Test the protected internals of {@link AttributeRouteCollector}. */
final class AttributeRouteCollectorInternalsTest {

    @Test
    void convertToDataParameterPreservesCastWhenPresent() {
        var collector = new AttributeRouteCollector();
        var withCast = new Parameter("id", "\\d+", new Cast("int"), false, true, null, null);
        var withoutCast = new Parameter("id", "\\d+", null, false, true, null, null);

        assertTrue(collector.convertToDataParameter(withCast).hasCast());
        assertFalse(collector.convertToDataParameter(withoutCast).hasCast());
    }
}
