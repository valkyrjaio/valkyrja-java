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
import io.valkyrja.tests.fixtures.dispatch.DispatchableFixture;
import io.valkyrja.throwable.exception.InvalidArgumentException;
import org.junit.jupiter.api.Test;

/** Test the {@link MethodDispatch} data object. */
final class MethodDispatchTest {

    @Test
    void twoArgConstructorDefaultsToInstanceMethod() {
        var dispatch = new MethodDispatch(DispatchableFixture.class, "instanceMethod");

        assertEquals("instanceMethod", dispatch.getMethod());
        assertFalse(dispatch.isStatic());
    }

    @Test
    void staticFlagConstructor() {
        var dispatch = new MethodDispatch(DispatchableFixture.class, "staticMethod", true);

        assertTrue(dispatch.isStatic());
    }

    @Test
    void fromArrayCreatesStaticDispatch() {
        var dispatch = MethodDispatch.fromArray(DispatchableFixture.class, "staticMethod");

        assertEquals("staticMethod", dispatch.getMethod());
        assertTrue(dispatch.isStatic());
    }

    @Test
    void fromArrayRejectsBlankMethodName() {
        assertThrows(
                InvalidArgumentException.class,
                () -> MethodDispatch.fromArray(DispatchableFixture.class, " "));
    }

    @Test
    void withMethodsReturnNewInstances() {
        var original = new MethodDispatch(DispatchableFixture.class, "instanceMethod");

        var withMethod = original.withMethod("other");
        var withStatic = original.withIsStatic(true);

        assertNotSame(original, withMethod);
        assertEquals("other", withMethod.getMethod());
        assertTrue(withStatic.isStatic());
        assertEquals("instanceMethod", original.getMethod());
    }

    @Test
    void toMapIncludesMethodAndStaticFlag() {
        var map = new MethodDispatch(DispatchableFixture.class, "staticMethod", true).toMap();

        assertEquals("staticMethod", map.get("method"));
        assertEquals(true, map.get("isStatic"));
        assertEquals(DispatchableFixture.class.getName(), map.get("class"));
    }

    @Test
    void toStringUsesStaticOrInstanceSeparator() {
        assertEquals(
                DispatchableFixture.class.getName() + "::staticMethod()",
                new MethodDispatch(DispatchableFixture.class, "staticMethod", true).toString());
        assertEquals(
                DispatchableFixture.class.getName() + "->instanceMethod()",
                new MethodDispatch(DispatchableFixture.class, "instanceMethod").toString());
    }

    @Test
    void fromArrayValidatesMethodName() {
        assertThrows(
                io.valkyrja.throwable.exception.InvalidArgumentException.class,
                () -> MethodDispatch.fromArray(DispatchableFixture.class, null));
        assertThrows(
                io.valkyrja.throwable.exception.InvalidArgumentException.class,
                () -> MethodDispatch.fromArray(DispatchableFixture.class, " "));
        assertNotNull(MethodDispatch.fromArray(DispatchableFixture.class, "instanceMethod"));
    }
}
