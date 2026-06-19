/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.dispatch.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.classes.dispatch.DispatchableClass;
import io.valkyrja.dispatch.data.ConstantDispatch;
import io.valkyrja.dispatch.throwable.exception.DispatchNoClassException;
import org.junit.jupiter.api.Test;

/** Test the {@link ConstantDispatch} data object. */
final class ConstantDispatchTest {

    @Test
    void constantWithoutClassName() {
        var dispatch = new ConstantDispatch("CONSTANT");

        assertEquals("CONSTANT", dispatch.getConstant());
        assertFalse(dispatch.hasClassName());
    }

    @Test
    void getClassNameThrowsWhenAbsent() {
        assertThrows(DispatchNoClassException.class, () -> new ConstantDispatch("CONSTANT").getClassName());
    }

    @Test
    void constantWithClassName() {
        var dispatch = new ConstantDispatch("CONSTANT", DispatchableClass.class);

        assertTrue(dispatch.hasClassName());
        assertSame(DispatchableClass.class, dispatch.getClassName());
    }

    @Test
    void withMethodsReturnNewInstances() {
        var original = new ConstantDispatch("CONSTANT", DispatchableClass.class);

        assertEquals("OTHER", original.withConstant("OTHER").getConstant());
        assertSame(String.class, original.withClassName(String.class).getClassName());
        assertFalse(original.withoutClassName().hasClassName());
        // original unchanged
        assertEquals("CONSTANT", original.getConstant());
        assertTrue(original.hasClassName());
    }

    @Test
    void toMapAndToStringWithClassName() {
        var dispatch = new ConstantDispatch("CONSTANT", DispatchableClass.class);

        var map = dispatch.toMap();
        assertEquals("CONSTANT", map.get("constant"));
        assertEquals(DispatchableClass.class.getName(), map.get("class"));
        assertEquals(DispatchableClass.class.getName() + "::CONSTANT", dispatch.toString());
    }

    @Test
    void toMapAndToStringWithoutClassName() {
        var dispatch = new ConstantDispatch("CONSTANT");

        assertFalse(dispatch.toMap().containsKey("class"));
        assertEquals("CONSTANT", dispatch.toString());
    }
}
