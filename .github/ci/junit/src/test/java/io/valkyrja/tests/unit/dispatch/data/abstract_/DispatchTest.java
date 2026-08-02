/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.dispatch.data.abstract_;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.valkyrja.dispatch.data.abstract_.Dispatch;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Test the {@link Dispatch}. */
final class DispatchTest {

    @Test
    void concreteSubclassImplementsContract() {
        var dispatch =
                new Dispatch() {
                    @Override
                    public Map<String, Object> toMap() {
                        return Map.of("k", "v");
                    }

                    @Override
                    public String toString() {
                        return "dispatch";
                    }
                };

        assertEquals("v", dispatch.toMap().get("k"));
        assertEquals("dispatch", dispatch.toString());
    }
}
