/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.container.manager;

import static org.junit.jupiter.api.Assertions.assertThrows;

import io.valkyrja.container.throwable.exception.ContainerCyclicAliasException;
import java.util.Map;
import org.junit.jupiter.api.Test;

final class NativeChildContainerTest {

    public static class Service {}

    public interface Greeter {}

    @Test
    void getAliasedThrowsWhenTheParentOwnMapHoldsACycle() {
        var parent = new Container();
        // bindAlias and setFromData both reject a cycle, so only a direct write to the
        // protected map reaches the bound this walk carries
        parent.aliases.put(Greeter.class, Service.class);
        parent.aliases.put(Service.class, Greeter.class);
        var child = new NativeChildContainer(parent);

        assertThrows(
                ContainerCyclicAliasException.class,
                () -> child.getAliased(Greeter.class, Map.of()));
    }
}
