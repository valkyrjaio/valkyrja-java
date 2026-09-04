/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.routing.caster;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import io.valkyrja.cli.interaction.argument.Argument;
import io.valkyrja.cli.interaction.enum_.OptionType;
import io.valkyrja.cli.interaction.option.Option;
import io.valkyrja.cli.routing.caster.Caster;
import io.valkyrja.cli.routing.data.ArgumentParameter;
import io.valkyrja.cli.routing.data.OptionParameter;
import io.valkyrja.container.manager.Container;
import io.valkyrja.tests.fixtures.type.TypeFixture;
import io.valkyrja.type.data.Cast;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Test the {@link Caster}. */
final class CasterTest {

    private static Container containerWithType() {
        var container = new Container();
        container.bind(TypeFixture.class, TypeFixture::make);
        return container;
    }

    @Test
    void returnsRawValuesWithoutACast() {
        var parameter = new ArgumentParameter("n", "d").withArguments(new Argument("a"));

        assertEquals(List.of("a"), new Caster(containerWithType()).getCastValues(parameter));
    }

    @Test
    void convertsEachValueWhenTheCastConverts() {
        var parameter =
                new ArgumentParameter("n", "d")
                        .withCast(new Cast(TypeFixture.class))
                        .withArguments(new Argument("a"), new Argument("b"));

        assertEquals(
                List.of("cast:a", "cast:b"),
                new Caster(containerWithType()).getCastValues(parameter));
    }

    @Test
    void returnsTheTypeWhenTheCastDoesNotConvert() {
        var parameter =
                new ArgumentParameter("n", "d")
                        .withCast(new Cast(TypeFixture.class, false, false))
                        .withArguments(new Argument("a"));

        var values = new Caster(containerWithType()).getCastValues(parameter);

        assertEquals(1, values.size());
        assertInstanceOf(TypeFixture.class, values.get(0));
    }

    @Test
    void castsAnOptionParameterTheSameWay() {
        var parameter =
                new OptionParameter("verbose", "d")
                        .withCast(new Cast(TypeFixture.class))
                        .withOptions(new Option("verbose", "a", OptionType.LONG));

        assertEquals(List.of("cast:a"), new Caster(containerWithType()).getCastValues(parameter));
    }

    @Test
    void buildsOneTypePerValueForASingletonBinding() {
        var container = new Container();
        container.bindSingleton(TypeFixture.class, TypeFixture::make);
        var parameter =
                new ArgumentParameter("n", "d")
                        .withCast(new Cast(TypeFixture.class))
                        .withArguments(new Argument("a"), new Argument("b"));

        assertEquals(List.of("cast:a", "cast:b"), new Caster(container).getCastValues(parameter));
    }
}
