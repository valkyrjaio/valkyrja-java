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
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.dispatch.data.PropertyDispatch;
import io.valkyrja.tests.fixtures.dispatch.DispatchableClass;
import org.junit.jupiter.api.Test;

/** Test the {@link PropertyDispatch} data object. */
final class PropertyDispatchTest {

    @Test
    void twoArgConstructorDefaultsToInstanceProperty() {
        var dispatch = new PropertyDispatch(DispatchableClass.class, "instanceField");

        assertEquals("instanceField", dispatch.getProperty());
        assertFalse(dispatch.isStatic());
    }

    @Test
    void staticFlagConstructor() {
        var dispatch = new PropertyDispatch(DispatchableClass.class, "staticField", true);

        assertTrue(dispatch.isStatic());
    }

    @Test
    void withMethodsReturnNewInstances() {
        var original = new PropertyDispatch(DispatchableClass.class, "instanceField");

        var withProperty = original.withProperty("other");
        var withStatic = original.withIsStatic(true);

        assertNotSame(original, withProperty);
        assertEquals("other", withProperty.getProperty());
        assertTrue(withStatic.isStatic());
        assertEquals("instanceField", original.getProperty());
    }

    @Test
    void toMapIncludesPropertyAndStaticFlag() {
        var map = new PropertyDispatch(DispatchableClass.class, "staticField", true).toMap();

        assertEquals("staticField", map.get("property"));
        assertEquals(true, map.get("isStatic"));
        assertEquals(DispatchableClass.class.getName(), map.get("class"));
    }

    @Test
    void toStringUsesStaticOrInstanceSeparator() {
        assertEquals(
                DispatchableClass.class.getName() + "::staticField",
                new PropertyDispatch(DispatchableClass.class, "staticField", true).toString());
        assertEquals(
                DispatchableClass.class.getName() + "->instanceField",
                new PropertyDispatch(DispatchableClass.class, "instanceField").toString());
    }
}
