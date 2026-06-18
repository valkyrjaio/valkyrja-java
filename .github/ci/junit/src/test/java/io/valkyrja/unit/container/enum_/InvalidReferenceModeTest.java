/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.container.enum_;
import io.valkyrja.container.enum_.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class InvalidReferenceModeTest {

    @Test
    void valuesAndValueOf() {
        assertEquals(2, InvalidReferenceMode.values().length);
        assertEquals(
                InvalidReferenceMode.THROW_EXCEPTION,
                InvalidReferenceMode.valueOf("THROW_EXCEPTION"));
        assertEquals(
                InvalidReferenceMode.NEW_INSTANCE_OR_THROW_EXCEPTION,
                InvalidReferenceMode.valueOf("NEW_INSTANCE_OR_THROW_EXCEPTION"));
    }
}