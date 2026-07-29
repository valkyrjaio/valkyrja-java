/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.reflection.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.valkyrja.reflection.support.Reflection;
import io.valkyrja.reflection.throwable.exception.ReflectionInvalidClassToInstantiateException;
import io.valkyrja.tests.fixtures.reflection.AbstractReflectableClass;
import io.valkyrja.tests.fixtures.reflection.NoDefaultConstructorClass;
import io.valkyrja.tests.fixtures.reflection.ReflectableClass;
import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;

/** Test the {@link Reflection} support utility. */
final class ReflectionTest {

    @Test
    void instantiateReturnsInstanceAndCaches() {
        var first = Reflection.instantiate(ReflectableClass.class);
        var second = Reflection.instantiate(ReflectableClass.class);

        assertNotNull(first);
        assertSame(first, second);
    }

    @Test
    void instantiateThrowsWhenClassCannotBeInstantiated() {
        assertThrows(
                ReflectionInvalidClassToInstantiateException.class,
                () -> Reflection.instantiate(AbstractReflectableClass.class));
    }

    @Test
    void constructorReturnsNoArgConstructorAndCaches() {
        Constructor<ReflectableClass> first = Reflection.constructor(ReflectableClass.class);
        Constructor<ReflectableClass> second = Reflection.constructor(ReflectableClass.class);

        assertNotNull(first);
        assertSame(first, second);
    }

    @Test
    void constructorThrowsWhenNoNoArgConstructorExists() {
        assertThrows(
                ReflectionInvalidClassToInstantiateException.class,
                () -> Reflection.constructor(NoDefaultConstructorClass.class));
    }

    @Test
    void methodResolvesAndCaches() throws Exception {
        var first = Reflection.method(ReflectableClass.class, "greet");
        var second = Reflection.method(ReflectableClass.class, "greet");

        assertNotNull(first);
        assertSame(first, second);
        assertEquals("hello", first.invoke(new ReflectableClass()));
    }

    @Test
    void methodThrowsWhenMethodDoesNotExist() {
        assertThrows(
                ReflectionInvalidClassToInstantiateException.class,
                () -> Reflection.method(ReflectableClass.class, "doesNotExist"));
    }

    @Test
    void privateConstructorIsInvocable() throws Exception {
        Constructor<Reflection> constructor = Reflection.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertNotNull(constructor.newInstance());
    }
}
