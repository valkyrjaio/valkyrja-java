/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.container.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.valkyrja.container.throwable.exception.ContainerCyclicAliasException;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

final class NativeChildContainerTest {

    public static class Service {}

    public interface Greeter {}

    @Test
    @Timeout(5)
    void getAliasedThrowsWhenTheParentOwnMapHoldsACycle() {
        var parent = new Container();
        // Two concurrent writes can leave this cycle, but no single-threaded sequence
        // can, so a direct write is what reaches the bound deterministically
        parent.aliases.put(Greeter.class, Service.class);
        parent.aliases.put(Service.class, Greeter.class);
        var child = new NativeChildContainer(parent);

        ContainerCyclicAliasException throwable =
                assertThrows(
                        ContainerCyclicAliasException.class,
                        () -> child.getAliased(Greeter.class, Map.of()));

        assertEquals(
                "Alias `io.valkyrja.container.manager.NativeChildContainerTest$Service` cannot"
                        + " reach `io.valkyrja.container.manager.NativeChildContainerTest$Greeter`,"
                        + " because the chain from"
                        + " `io.valkyrja.container.manager.NativeChildContainerTest$Greeter`"
                        + " returns to"
                        + " `io.valkyrja.container.manager.NativeChildContainerTest$Service`.",
                throwable.getMessage());
    }
}
