/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.routing.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.routing.data.Parameter;
import io.valkyrja.http.routing.throwable.exception.HttpRoutingNoCastException;
import io.valkyrja.type.data.Cast;
import org.junit.jupiter.api.Test;

/** Test the http routing {@link Parameter}. */
final class ParameterTest {

    @Test
    void shortConstructorDefaults() {
        var parameter = new Parameter("id", "\\d+");

        assertEquals("id", parameter.getName());
        assertEquals("\\d+", parameter.getRegex());
        assertFalse(parameter.hasCast());
        assertFalse(parameter.isOptional());
        assertTrue(parameter.shouldCapture());
    }

    @Test
    void getCastThrowsWhenAbsent() {
        assertThrows(HttpRoutingNoCastException.class, () -> new Parameter("id", "\\d+").getCast());
    }

    @Test
    void withMethodsReturnCopies() {
        var parameter = new Parameter("id", "\\d+");
        var cast = new Cast("int");

        assertEquals("slug", parameter.withName("slug").getName());
        assertEquals("\\w+", parameter.withRegex("\\w+").getRegex());
        assertSame(cast, parameter.withCast(cast).getCast());
        assertTrue(parameter.withIsOptional(true).isOptional());
        assertFalse(parameter.withShouldCapture(false).shouldCapture());
        assertEquals("default", parameter.withDefault("default").getDefault());
        assertEquals("v", parameter.withValue("v").getValue());
        // original unchanged
        assertEquals("id", parameter.getName());
        assertFalse(parameter.hasCast());
    }

    @Test
    void fullConstructor() {
        var cast = new Cast("int");
        var parameter = new Parameter("id", "\\d+", cast, true, false, "d", "v");

        assertTrue(parameter.hasCast());
        assertTrue(parameter.isOptional());
        assertFalse(parameter.shouldCapture());
        assertEquals("d", parameter.getDefault());
        assertEquals("v", parameter.getValue());
    }
}
