/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.http.message.enum_;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import io.valkyrja.http.message.enum_.StatusText;
import org.junit.jupiter.api.Test;

/** Test the {@link StatusText} enum. */
final class StatusTextTest {

    @Test
    void everyConstantHasAValueAndResolvesByName() {
        for (StatusText text : StatusText.values()) {
            assertNotNull(text.getValue());
            assertSame(text, StatusText.valueOf(text.name()));
        }
    }
}
