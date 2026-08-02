/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.struct.request.abstract_;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.param.ParsedBodyParamCollection;
import io.valkyrja.http.message.param.ParsedJsonParamCollection;
import io.valkyrja.http.message.param.QueryParamCollection;
import io.valkyrja.http.message.request.JsonServerRequest;
import io.valkyrja.http.message.request.ServerRequest;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.struct.throwable.exception.HttpStructJsonServerRequestExpectedException;
import io.valkyrja.tests.fixtures.http.struct.JsonStructFixture;
import io.valkyrja.tests.fixtures.http.struct.ParsedBodyStructFixture;
import io.valkyrja.tests.fixtures.http.struct.QueryStructFixture;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Test the abstract {@code RequestStruct} hierarchy via concrete fixtures. */
final class RequestStructTest {

    @Test
    void parsedBodyStructSelectsConfiguredFields() {
        ServerRequestContract request =
                new ServerRequest()
                        .withParsedBody(
                                new ParsedBodyParamCollection(
                                        Map.of("name", "bob", "email", "e", "extra", "x")));
        var struct = new ParsedBodyStructFixture();

        assertEquals(2, struct.getDataFromRequest(request).size());
        assertTrue(struct.determineIfRequestContainsExtraData(request));
        assertTrue(struct.getValidationRules(request).isEmpty());
        assertTrue(struct.validate(request).validateRules());
    }

    @Test
    void queryStructSelectsConfiguredFields() {
        ServerRequestContract request =
                new ServerRequest().withQueryParams(new QueryParamCollection(Map.of("page", "2")));
        var struct = new QueryStructFixture();

        assertTrue(struct.getDataFromRequest(request).containsKey("page"));
        assertFalse(struct.determineIfRequestContainsExtraData(request));
    }

    @Test
    void jsonStructReadsParsedJson() {
        ServerRequestContract request =
                new JsonServerRequest()
                        .withParsedJson(
                                new ParsedJsonParamCollection(Map.of("name", "bob", "extra", "x")));
        var struct = new JsonStructFixture();

        assertTrue(struct.getDataFromRequest(request).containsKey("name"));
        assertTrue(struct.determineIfRequestContainsExtraData(request));
    }

    @Test
    void validatesDeclaredRules() {
        ServerRequestContract request =
                new ServerRequest()
                        .withParsedBody(new ParsedBodyParamCollection(Map.of("name", "bob")));
        var struct = new io.valkyrja.tests.fixtures.http.struct.ValidatingStructFixture();

        assertFalse(struct.getValidationRules(request).isEmpty());
        assertTrue(struct.validate(request).validateRules());
    }

    @Test
    void jsonStructRequiresJsonRequest() {
        var struct = new JsonStructFixture();

        assertThrows(
                HttpStructJsonServerRequestExpectedException.class,
                () -> struct.getDataFromRequest(new ServerRequest()));
    }

    @Test
    void validateRulesPassesWhenNoRulesDeclared() {
        assertTrue(new QueryStructFixture().validate(new ServerRequest()).validateRules());
    }

    @Test
    void validateFailsWhenRulesDoNotPass() {
        var struct = new io.valkyrja.tests.fixtures.http.struct.FailingValidationStructFixture();

        assertFalse(struct.validate(new ServerRequest()).validateRules());
    }
}
