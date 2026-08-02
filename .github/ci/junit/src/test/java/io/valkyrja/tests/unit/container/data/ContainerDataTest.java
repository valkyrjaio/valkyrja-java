/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.container.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.container.data.*;
import io.valkyrja.container.manager.contract.ContainerContract;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

final class ContainerDataTest {

    @Test
    void defaultIsEmpty() {
        var data = new ContainerData();

        assertTrue(data.aliases().isEmpty());
        assertTrue(data.callbacks().isEmpty());
        assertTrue(data.services().isEmpty());
        assertTrue(data.singletons().isEmpty());
    }

    @Test
    void retainsProvidedBindings() {
        Map<Class<?>, Class<?>> aliases = Map.of(CharSequence.class, String.class);
        Map<Class<?>, Consumer<ContainerContract>> callbacks = Map.of(String.class, c -> {});
        Map<Class<?>, BiFunction<ContainerContract, Map<String, Object>, Object>> services =
                Map.of(String.class, (c, args) -> "value");
        Map<Class<?>, Class<?>> singletons = Map.of(String.class, String.class);

        var data = new ContainerData(aliases, callbacks, services, singletons);

        assertEquals(aliases, data.aliases());
        assertEquals(callbacks, data.callbacks());
        assertEquals(services, data.services());
        assertEquals(singletons, data.singletons());
    }
}
