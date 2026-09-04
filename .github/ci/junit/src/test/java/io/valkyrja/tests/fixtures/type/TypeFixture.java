/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.type;

import io.valkyrja.cli.routing.constant.CastArgument;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.type.contract.TypeContract;
import java.util.Map;
import java.util.function.UnaryOperator;
import org.jspecify.annotations.Nullable;

/** A type that prefixes its value, so a test can tell a cast value from a raw one. */
public final class TypeFixture implements TypeContract {

    private final String value;

    public TypeFixture(String value) {
        this.value = value;
    }

    public static TypeFixture make(ContainerContract container, Map<String, Object> arguments) {
        return new TypeFixture(String.valueOf(arguments.get(CastArgument.VALUE)));
    }

    @Override
    public @Nullable Object asValue() {
        return "cast:" + value;
    }

    @Override
    public @Nullable Object asFlatValue() {
        return "cast:" + value;
    }

    @Override
    public TypeContract modify(UnaryOperator<@Nullable Object> closure) {
        return new TypeFixture(String.valueOf(closure.apply(value)));
    }
}
