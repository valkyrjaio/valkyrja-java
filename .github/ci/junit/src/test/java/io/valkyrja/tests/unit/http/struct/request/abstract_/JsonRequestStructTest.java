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

import io.valkyrja.http.message.param.ParsedJsonParamCollection;
import io.valkyrja.http.message.request.JsonServerRequest;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.struct.request.abstract_.JsonRequestStruct;
import io.valkyrja.tests.fixtures.http.struct.JsonStructFixture;
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
        var struct = new JsonStructFixture();

        assertFalse(struct.getDataFromRequest(request).isEmpty());
        assertTrue(struct.determineIfRequestContainsExtraData(request));
    }
}
