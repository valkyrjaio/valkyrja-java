/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.tests.unit.http.struct.request.abstract_;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.param.ParsedBodyParamCollection;
import io.valkyrja.http.message.request.ServerRequest;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.struct.request.abstract_.ParsedBodyRequestStruct;
import io.valkyrja.tests.fixtures.http.struct.ParsedBodyStructClass;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Test the {@link ParsedBodyRequestStruct}. */
final class ParsedBodyRequestStructTest {

    @Test
    void selectsOnlyAndDetectsExtraFields() {
        ServerRequestContract request =
                new ServerRequest()
                        .withParsedBody(
                                new ParsedBodyParamCollection(
                                        Map.of("name", "bob", "email", "e", "extra", "x")));
        var struct = new ParsedBodyStructClass();

        assertFalse(struct.getDataFromRequest(request).isEmpty());
        assertTrue(struct.determineIfRequestContainsExtraData(request));
    }
}
