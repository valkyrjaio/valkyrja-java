/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.routing.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.cli.interaction.argument.Argument;
import io.valkyrja.cli.routing.data.ArgumentParameter;
import io.valkyrja.cli.routing.data.contract.ArgumentParameterContract;
import io.valkyrja.cli.routing.enum_.ArgumentMode;
import io.valkyrja.cli.routing.enum_.ArgumentValueMode;
import io.valkyrja.cli.routing.throwable.exception.CliRoutingArgumentValuesValidationException;
import io.valkyrja.cli.routing.throwable.exception.CliRoutingNoCastException;
import io.valkyrja.tests.fixtures.type.TypeFixture;
import io.valkyrja.type.data.Cast;
import org.junit.jupiter.api.Test;

/** Test the {@link ArgumentParameter}. */
final class ArgumentParameterTest {

    @Test
    void defaultsAndBaseAccessors() {
        var param = new ArgumentParameter("name", "description");

        assertEquals("name", param.getName());
        assertEquals("description", param.getDescription());
        assertSame(ArgumentMode.OPTIONAL, param.getMode());
        assertSame(ArgumentValueMode.DEFAULT, param.getValueMode());
        assertTrue(param.getArguments().isEmpty());
        assertEquals("renamed", param.withName("renamed").getName());
        assertEquals("desc", param.withDescription("desc").getDescription());
    }

    @Test
    void modeMutations() {
        var param = new ArgumentParameter("n", "d");

        assertSame(ArgumentMode.REQUIRED, param.withMode(ArgumentMode.REQUIRED).getMode());
        assertSame(
                ArgumentValueMode.ARRAY,
                param.withValueMode(ArgumentValueMode.ARRAY).getValueMode());
    }

    @Test
    void argumentMutationsAndValues() {
        var param =
                new ArgumentParameter("n", "d")
                        .withArguments(new Argument("a"))
                        .withAddedArguments(new Argument("b"));

        assertEquals(2, param.getArguments().size());
        assertEquals(java.util.List.of("a", "b"), param.getValues());
        assertTrue(param.hasFirstValue());
        assertEquals("a", param.getFirstValue());
    }

    @Test
    void firstValueEmptyWhenNoArguments() {
        var param = new ArgumentParameter("n", "d");

        assertFalse(param.hasFirstValue());
        assertEquals("", param.getFirstValue());
    }

    @Test
    void areValuesValidForRequiredAndDefault() {
        var required = new ArgumentParameter("n", "d").withMode(ArgumentMode.REQUIRED);
        assertFalse(((ArgumentParameter) required).areValuesValid());

        var requiredWithArg = ((ArgumentParameter) required).withArguments(new Argument("a"));
        assertTrue(((ArgumentParameter) requiredWithArg).areValuesValid());

        var defaultTooMany =
                new ArgumentParameter("n", "d").withArguments(new Argument("a"), new Argument("b"));
        assertFalse(((ArgumentParameter) defaultTooMany).areValuesValid());

        var arrayMode =
                ((ArgumentParameter)
                                new ArgumentParameter("n", "d")
                                        .withValueMode(ArgumentValueMode.ARRAY))
                        .withArguments(new Argument("a"), new Argument("b"));
        assertTrue(((ArgumentParameter) arrayMode).areValuesValid());
    }

    @Test
    void validateValuesReturnsSelfWhenValidAndThrowsWhenInvalid() {
        var valid = new ArgumentParameter("n", "d");
        assertSame(valid, valid.validateValues());

        var invalid =
                (ArgumentParameter) new ArgumentParameter("n", "d").withMode(ArgumentMode.REQUIRED);
        assertThrows(CliRoutingArgumentValuesValidationException.class, invalid::validateValues);
    }

    @Test
    void isProvidedSeparatesThePresenceFromTheValue() {
        ArgumentParameter parameter = new ArgumentParameter("target", "The target");

        assertFalse(parameter.isProvided());
        assertFalse(parameter.hasFirstValue());

        ArgumentParameterContract empty = parameter.withArguments(new Argument(""));

        assertTrue(empty.isProvided());
        assertFalse(empty.hasFirstValue());

        ArgumentParameterContract withValue = parameter.withArguments(new Argument("host"));

        assertTrue(withValue.isProvided());
        assertTrue(withValue.hasFirstValue());
    }

    @Test
    void castAccessorsReportAndClearTheCast() {
        var cast = new Cast(TypeFixture.class);
        var param = new ArgumentParameter("n", "d");

        assertFalse(param.hasCast());
        assertThrows(CliRoutingNoCastException.class, param::getCast);

        var withCast = param.withCast(cast);

        assertTrue(withCast.hasCast());
        assertSame(cast, withCast.getCast());
        assertFalse(withCast.withoutCast().hasCast());
    }
}
