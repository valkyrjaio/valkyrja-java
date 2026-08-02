/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.dispatch.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import io.valkyrja.tests.fixtures.dispatch.DispatchableFixture;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Package-private test for {@link ClassDispatch#copy()}, which is {@code protected} and not reached
 * through the public API (subclasses override it). Placed in the source package per the project's
 * package-private testing convention.
 */
final class ClassDispatchCopyTest {

    @Test
    void copyReturnsEquivalentInstance() {
        var original =
                new ClassDispatch(DispatchableFixture.class, Map.of("a", 1), List.of(String.class));

        var copy = original.copy();

        assertNotSame(original, copy);
        assertEquals(original.getClassName(), copy.getClassName());
        assertEquals(original.getArguments(), copy.getArguments());
        assertEquals(original.getDependencies(), copy.getDependencies());
    }
}
