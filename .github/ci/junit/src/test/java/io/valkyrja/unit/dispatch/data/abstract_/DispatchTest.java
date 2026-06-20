/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.dispatch.data.abstract_;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.valkyrja.dispatch.data.abstract_.Dispatch;
import java.util.Map;
import io.valkyrja.dispatch.data.abstract_.Dispatch;
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
