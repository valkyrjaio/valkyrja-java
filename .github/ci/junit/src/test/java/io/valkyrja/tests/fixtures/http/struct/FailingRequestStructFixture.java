/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.http.struct;

import io.valkyrja.http.struct.request.abstract_.ParsedBodyRequestStruct;
import java.util.List;
import java.util.Map;

/** A request struct with no no-arg constructor, so reflective instantiation fails. */
public final class FailingRequestStructFixture extends ParsedBodyRequestStruct {

    @SuppressWarnings("unused")
    public FailingRequestStructFixture(String required) {}

    @Override
    public Map<String, String> asMap() {
        return Map.of();
    }

    @Override
    public List<String> values() {
        return List.of();
    }
}
