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

import io.valkyrja.dispatch.data.CallableDispatch;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

/** Test the {@link CallableDispatch} data object. */
final class CallableDispatchTest {

    private static final Function<Object[], Object> CALLABLE = args -> "result";

    @Test
    void defaultsToEmptyArgumentsAndDependencies() {
        var dispatch = new CallableDispatch(CALLABLE);

        assertSame(CALLABLE, dispatch.getCallable());
        assertTrue(dispatch.getArguments().isEmpty());
        assertTrue(dispatch.getDependencies().isEmpty());
    }

    @Test
    void withMethodsReturnNewInstances() {
        var original = new CallableDispatch(CALLABLE);
        Function<Object[], Object> other = args -> "other";

        assertNotSame(original, original.withCallable(other));
        assertSame(other, original.withCallable(other).getCallable());
        assertEquals(Map.of("a", 1), original.withArguments(Map.of("a", 1)).getArguments());
        assertEquals(
                List.of(String.class),
                original.withDependencies(List.of(String.class)).getDependencies());
    }

    @Test
    void toMapAndToString() {
        var dispatch = new CallableDispatch(CALLABLE, Map.of("a", 1), List.of(String.class));

        var map = dispatch.toMap();
        assertEquals(CALLABLE.toString(), map.get("callable"));
        assertEquals(Map.of("a", 1), map.get("arguments"));
        assertEquals(List.of(String.class.getName()), map.get("dependencies"));
        assertEquals(CALLABLE.toString(), dispatch.toString());
    }
}
