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

import io.valkyrja.http.message.param.ParsedJsonParamCollection;
import io.valkyrja.http.message.request.JsonServerRequest;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.struct.request.abstract_.JsonRequestStruct;
import io.valkyrja.tests.fixtures.http.struct.JsonStructClass;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Test the {@link JsonRequestStruct}. */
final class JsonRequestStructTest {

    @Test
    void selectsOnlyAndDetectsExtraFields() {
        ServerRequestContract request =
                new JsonServerRequest()
                        .withParsedJson(
                                new ParsedJsonParamCollection(Map.of("name", "bob", "extra", "x")));
        var struct = new JsonStructClass();

        assertFalse(struct.getDataFromRequest(request).isEmpty());
        assertTrue(struct.determineIfRequestContainsExtraData(request));
    }
}
