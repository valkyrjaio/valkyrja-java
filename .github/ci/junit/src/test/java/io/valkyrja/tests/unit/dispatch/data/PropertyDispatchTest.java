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
import io.valkyrja.tests.fixtures.dispatch.DispatchableFixture;
import org.junit.jupiter.api.Test;

/** Test the {@link PropertyDispatch} data object. */
final class PropertyDispatchTest {

    @Test
    void twoArgConstructorDefaultsToInstanceProperty() {
        var dispatch = new PropertyDispatch(DispatchableFixture.class, "instanceField");

        assertEquals("instanceField", dispatch.getProperty());
        assertFalse(dispatch.isStatic());
    }

    @Test
    void staticFlagConstructor() {
        var dispatch = new PropertyDispatch(DispatchableFixture.class, "staticField", true);

        assertTrue(dispatch.isStatic());
    }

    @Test
    void withMethodsReturnNewInstances() {
        var original = new PropertyDispatch(DispatchableFixture.class, "instanceField");

        var withProperty = original.withProperty("other");
        var withStatic = original.withIsStatic(true);

        assertNotSame(original, withProperty);
        assertEquals("other", withProperty.getProperty());
        assertTrue(withStatic.isStatic());
        assertEquals("instanceField", original.getProperty());
    }

    @Test
    void toMapIncludesPropertyAndStaticFlag() {
        var map = new PropertyDispatch(DispatchableFixture.class, "staticField", true).toMap();

        assertEquals("staticField", map.get("property"));
        assertEquals(true, map.get("isStatic"));
        assertEquals(DispatchableFixture.class.getName(), map.get("class"));
    }

    @Test
    void toStringUsesStaticOrInstanceSeparator() {
        assertEquals(
                DispatchableFixture.class.getName() + "::staticField",
                new PropertyDispatch(DispatchableFixture.class, "staticField", true).toString());
        assertEquals(
                DispatchableFixture.class.getName() + "->instanceField",
                new PropertyDispatch(DispatchableFixture.class, "instanceField").toString());
    }
}
