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
import io.valkyrja.tests.fixtures.container.ServiceFixture;
import io.valkyrja.tests.fixtures.container.SingletonFixture;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

final class NativeChildContainerTest {

    @Test
    @Timeout(5)
    void getAliasedThrowsWhenTheParentOwnMapHoldsACycle() {
        var parent = new Container();
        // Two concurrent writes can leave this cycle, but no single-threaded sequence
        // can, so a direct write is what reaches the bound deterministically
        parent.aliases.put(SingletonFixture.class, ServiceFixture.class);
        parent.aliases.put(ServiceFixture.class, SingletonFixture.class);
        var child = new NativeChildContainer(parent);

        ContainerCyclicAliasException throwable =
                assertThrows(
                        ContainerCyclicAliasException.class,
                        () -> child.getAliased(SingletonFixture.class, Map.of()));

        assertEquals(
                "Alias `"
                        + ServiceFixture.class.getName()
                        + "` cannot reach `"
                        + SingletonFixture.class.getName()
                        + "`, because the chain from `"
                        + SingletonFixture.class.getName()
                        + "` returns to `"
                        + ServiceFixture.class.getName()
                        + "`.",
                throwable.getMessage());
    }
}
