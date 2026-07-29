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
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.dispatch.data.ClassDispatch;
import io.valkyrja.tests.fixtures.dispatch.DispatchableFixture;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Test the {@link ClassDispatch} data object. */
final class ClassDispatchTest {

    @Test
    void defaultsToEmptyArgumentsAndDependencies() {
        var dispatch = new ClassDispatch(DispatchableFixture.class);

        assertSame(DispatchableFixture.class, dispatch.getClassName());
        assertTrue(dispatch.getArguments().isEmpty());
        assertTrue(dispatch.getDependencies().isEmpty());
    }

    @Test
    void withMethodsReturnNewInstancesAndLeaveOriginalUnchanged() {
        var original = new ClassDispatch(DispatchableFixture.class);

        var withArgs = original.withArguments(Map.of("a", 1));
        var withDeps = original.withDependencies(List.of(String.class));
        var withName = original.withClassName(String.class);

        assertNotSame(original, withArgs);
        assertEquals(Map.of("a", 1), withArgs.getArguments());
        assertEquals(List.of(String.class), withDeps.getDependencies());
        assertSame(String.class, withName.getClassName());
        // original is immutable
        assertTrue(original.getArguments().isEmpty());
        assertTrue(original.getDependencies().isEmpty());
    }

    @Test
    void toMapContainsClassArgumentsAndDependencies() {
        var dispatch =
                new ClassDispatch(DispatchableFixture.class, Map.of("a", 1), List.of(String.class));

        var map = dispatch.toMap();

        assertEquals(DispatchableFixture.class.getName(), map.get("class"));
        assertEquals(Map.of("a", 1), map.get("arguments"));
        assertEquals(List.of(String.class.getName()), map.get("dependencies"));
    }

    @Test
    void toStringIsClassName() {
        assertEquals(
                DispatchableFixture.class.getName(),
                new ClassDispatch(DispatchableFixture.class).toString());
    }
}
