/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.http.struct;

import io.valkyrja.http.struct.request.abstract_.QueryRequestStruct;
import java.util.List;
import java.util.Map;

/** Concrete query request struct selecting the page field. */
public final class QueryStructFixture extends QueryRequestStruct {

    @Override
    public Map<String, String> asMap() {
        return Map.of("page", "page");
    }

    @Override
    public List<String> values() {
        return List.of("page");
    }
}
