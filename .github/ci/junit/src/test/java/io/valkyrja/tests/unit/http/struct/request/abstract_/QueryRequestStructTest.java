/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.struct.request.abstract_;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.param.QueryParamCollection;
import io.valkyrja.http.message.request.ServerRequest;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.struct.request.abstract_.QueryRequestStruct;
import io.valkyrja.tests.fixtures.http.struct.QueryStructFixture;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Test the {@link QueryRequestStruct}. */
final class QueryRequestStructTest {

    @Test
    void selectsOnlyAndDetectsExtraFields() {
        ServerRequestContract request =
                new ServerRequest()
                        .withQueryParams(
                                new QueryParamCollection(Map.of("page", "2", "extra", "x")));
        var struct = new QueryStructFixture();

        assertFalse(struct.getDataFromRequest(request).isEmpty());
        assertTrue(struct.determineIfRequestContainsExtraData(request));
    }
}
