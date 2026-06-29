/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.struct.request.abstract_;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.fixtures.http.struct.QueryStructClass;
import io.valkyrja.http.message.param.QueryParamCollection;
import io.valkyrja.http.message.request.ServerRequest;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import java.util.Map;
import io.valkyrja.http.struct.request.abstract_.QueryRequestStruct;
import org.junit.jupiter.api.Test;

/** Test the {@link QueryRequestStruct}. */
final class QueryRequestStructTest {

    @Test
    void selectsOnlyAndDetectsExtraFields() {
        ServerRequestContract request =
                new ServerRequest()
                        .withQueryParams(
                                new QueryParamCollection(Map.of("page", "2", "extra", "x")));
        var struct = new QueryStructClass();

        assertFalse(struct.getDataFromRequest(request).isEmpty());
        assertTrue(struct.determineIfRequestContainsExtraData(request));
    }
}
