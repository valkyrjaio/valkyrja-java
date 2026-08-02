/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.reflection.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.valkyrja.reflection.support.Reflection;
import io.valkyrja.reflection.throwable.exception.ReflectionInvalidClassToInstantiateException;
import io.valkyrja.tests.fixtures.reflection.NoDefaultConstructorFixture;
import io.valkyrja.tests.fixtures.reflection.ReflectableFixture;
import io.valkyrja.tests.fixtures.reflection.abstract_.NonInstantiableFixture;
import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;

/** Test the {@link Reflection} support utility. */
final class ReflectionTest {

    @Test
    void instantiateReturnsInstanceAndCaches() {
        var first = Reflection.instantiate(ReflectableFixture.class);
        var second = Reflection.instantiate(ReflectableFixture.class);

        assertNotNull(first);
        assertSame(first, second);
    }

    @Test
    void instantiateThrowsWhenClassCannotBeInstantiated() {
        assertThrows(
                ReflectionInvalidClassToInstantiateException.class,
                () -> Reflection.instantiate(NonInstantiableFixture.class));
    }

    @Test
    void constructorReturnsNoArgConstructorAndCaches() {
        Constructor<ReflectableFixture> first = Reflection.constructor(ReflectableFixture.class);
        Constructor<ReflectableFixture> second = Reflection.constructor(ReflectableFixture.class);

        assertNotNull(first);
        assertSame(first, second);
    }

    @Test
    void constructorThrowsWhenNoNoArgConstructorExists() {
        assertThrows(
                ReflectionInvalidClassToInstantiateException.class,
                () -> Reflection.constructor(NoDefaultConstructorFixture.class));
    }

    @Test
    void methodResolvesAndCaches() throws Exception {
        var first = Reflection.method(ReflectableFixture.class, "greet");
        var second = Reflection.method(ReflectableFixture.class, "greet");

        assertNotNull(first);
        assertSame(first, second);
        assertEquals("hello", first.invoke(new ReflectableFixture()));
    }

    @Test
    void methodThrowsWhenMethodDoesNotExist() {
        assertThrows(
                ReflectionInvalidClassToInstantiateException.class,
                () -> Reflection.method(ReflectableFixture.class, "doesNotExist"));
    }

    @Test
    void privateConstructorIsInvocable() throws Exception {
        Constructor<Reflection> constructor = Reflection.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertNotNull(constructor.newInstance());
    }
}
