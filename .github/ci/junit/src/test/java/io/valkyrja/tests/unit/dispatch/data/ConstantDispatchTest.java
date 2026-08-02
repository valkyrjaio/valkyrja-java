/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.dispatch.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.dispatch.data.ConstantDispatch;
import io.valkyrja.dispatch.throwable.exception.DispatchNoClassException;
import io.valkyrja.tests.fixtures.dispatch.DispatchableFixture;
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
        assertThrows(
                DispatchNoClassException.class,
                () -> new ConstantDispatch("CONSTANT").getClassName());
    }

    @Test
    void constantWithClassName() {
        var dispatch = new ConstantDispatch("CONSTANT", DispatchableFixture.class);

        assertTrue(dispatch.hasClassName());
        assertSame(DispatchableFixture.class, dispatch.getClassName());
    }

    @Test
    void withMethodsReturnNewInstances() {
        var original = new ConstantDispatch("CONSTANT", DispatchableFixture.class);

        assertEquals("OTHER", original.withConstant("OTHER").getConstant());
        assertSame(String.class, original.withClassName(String.class).getClassName());
        assertFalse(original.withoutClassName().hasClassName());
        // original unchanged
        assertEquals("CONSTANT", original.getConstant());
        assertTrue(original.hasClassName());
    }

    @Test
    void toMapAndToStringWithClassName() {
        var dispatch = new ConstantDispatch("CONSTANT", DispatchableFixture.class);

        var map = dispatch.toMap();
        assertEquals("CONSTANT", map.get("constant"));
        assertEquals(DispatchableFixture.class.getName(), map.get("class"));
        assertEquals(DispatchableFixture.class.getName() + "::CONSTANT", dispatch.toString());
    }

    @Test
    void toMapAndToStringWithoutClassName() {
        var dispatch = new ConstantDispatch("CONSTANT");

        assertFalse(dispatch.toMap().containsKey("class"));
        assertEquals("CONSTANT", dispatch.toString());
    }
}
