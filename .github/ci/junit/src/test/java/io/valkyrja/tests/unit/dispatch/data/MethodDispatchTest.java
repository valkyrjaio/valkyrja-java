/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.dispatch.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.dispatch.data.MethodDispatch;
import io.valkyrja.tests.fixtures.dispatch.DispatchableClass;
import io.valkyrja.throwable.exception.InvalidArgumentException;
import org.junit.jupiter.api.Test;

/** Test the {@link MethodDispatch} data object. */
final class MethodDispatchTest {

    @Test
    void twoArgConstructorDefaultsToInstanceMethod() {
        var dispatch = new MethodDispatch(DispatchableClass.class, "instanceMethod");

        assertEquals("instanceMethod", dispatch.getMethod());
        assertFalse(dispatch.isStatic());
    }

    @Test
    void staticFlagConstructor() {
        var dispatch = new MethodDispatch(DispatchableClass.class, "staticMethod", true);

        assertTrue(dispatch.isStatic());
    }

    @Test
    void fromArrayCreatesStaticDispatch() {
        var dispatch = MethodDispatch.fromArray(DispatchableClass.class, "staticMethod");

        assertEquals("staticMethod", dispatch.getMethod());
        assertTrue(dispatch.isStatic());
    }

    @Test
    void fromArrayRejectsBlankMethodName() {
        assertThrows(
                InvalidArgumentException.class,
                () -> MethodDispatch.fromArray(DispatchableClass.class, " "));
    }

    @Test
    void withMethodsReturnNewInstances() {
        var original = new MethodDispatch(DispatchableClass.class, "instanceMethod");

        var withMethod = original.withMethod("other");
        var withStatic = original.withIsStatic(true);

        assertNotSame(original, withMethod);
        assertEquals("other", withMethod.getMethod());
        assertTrue(withStatic.isStatic());
        assertEquals("instanceMethod", original.getMethod());
    }

    @Test
    void toMapIncludesMethodAndStaticFlag() {
        var map = new MethodDispatch(DispatchableClass.class, "staticMethod", true).toMap();

        assertEquals("staticMethod", map.get("method"));
        assertEquals(true, map.get("isStatic"));
        assertEquals(DispatchableClass.class.getName(), map.get("class"));
    }

    @Test
    void toStringUsesStaticOrInstanceSeparator() {
        assertEquals(
                DispatchableClass.class.getName() + "::staticMethod()",
                new MethodDispatch(DispatchableClass.class, "staticMethod", true).toString());
        assertEquals(
                DispatchableClass.class.getName() + "->instanceMethod()",
                new MethodDispatch(DispatchableClass.class, "instanceMethod").toString());
    }

    @Test
    void fromArrayValidatesMethodName() {
        assertThrows(
                io.valkyrja.throwable.exception.InvalidArgumentException.class,
                () -> MethodDispatch.fromArray(DispatchableClass.class, null));
        assertThrows(
                io.valkyrja.throwable.exception.InvalidArgumentException.class,
                () -> MethodDispatch.fromArray(DispatchableClass.class, " "));
        assertNotNull(MethodDispatch.fromArray(DispatchableClass.class, "instanceMethod"));
    }
}
