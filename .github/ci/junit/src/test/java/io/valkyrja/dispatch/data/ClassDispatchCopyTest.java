/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.dispatch.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import io.valkyrja.fixtures.dispatch.DispatchableClass;
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
                new ClassDispatch(DispatchableClass.class, Map.of("a", 1), List.of(String.class));

        var copy = original.copy();

        assertNotSame(original, copy);
        assertEquals(original.getClassName(), copy.getClassName());
        assertEquals(original.getArguments(), copy.getArguments());
        assertEquals(original.getDependencies(), copy.getDependencies());
    }
}
