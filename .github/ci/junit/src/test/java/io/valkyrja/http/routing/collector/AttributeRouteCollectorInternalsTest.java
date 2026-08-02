/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
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
