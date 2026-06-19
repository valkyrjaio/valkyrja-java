/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.struct.request.abstract_;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.classes.http.struct.JsonStructClass;
import io.valkyrja.classes.http.struct.ParsedBodyStructClass;
import io.valkyrja.classes.http.struct.QueryStructClass;
import io.valkyrja.http.message.param.ParsedBodyParamCollection;
import io.valkyrja.http.message.param.ParsedJsonParamCollection;
import io.valkyrja.http.message.param.QueryParamCollection;
import io.valkyrja.http.message.request.JsonServerRequest;
import io.valkyrja.http.message.request.ServerRequest;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.struct.throwable.exception.HttpStructJsonServerRequestExpectedException;
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
        var struct = new ParsedBodyStructClass();

        assertEquals(2, struct.getDataFromRequest(request).size());
        assertTrue(struct.determineIfRequestContainsExtraData(request));
        assertTrue(struct.getValidationRules(request).isEmpty());
        assertTrue(struct.validate(request).validateRules());
    }

    @Test
    void queryStructSelectsConfiguredFields() {
        ServerRequestContract request =
                new ServerRequest()
                        .withQueryParams(new QueryParamCollection(Map.of("page", "2")));
        var struct = new QueryStructClass();

        assertTrue(struct.getDataFromRequest(request).containsKey("page"));
        assertFalse(struct.determineIfRequestContainsExtraData(request));
    }

    @Test
    void jsonStructReadsParsedJson() {
        ServerRequestContract request =
                new JsonServerRequest()
                        .withParsedJson(
                                new ParsedJsonParamCollection(Map.of("name", "bob", "extra", "x")));
        var struct = new JsonStructClass();

        assertTrue(struct.getDataFromRequest(request).containsKey("name"));
        assertTrue(struct.determineIfRequestContainsExtraData(request));
    }

    @Test
    void validatesDeclaredRules() {
        ServerRequestContract request =
                new ServerRequest()
                        .withParsedBody(new ParsedBodyParamCollection(Map.of("name", "bob")));
        var struct = new io.valkyrja.classes.http.struct.ValidatingStructClass();

        assertFalse(struct.getValidationRules(request).isEmpty());
        assertTrue(struct.validate(request).validateRules());
    }

    @Test
    void jsonStructRequiresJsonRequest() {
        var struct = new JsonStructClass();

        assertThrows(
                HttpStructJsonServerRequestExpectedException.class,
                () -> struct.getDataFromRequest(new ServerRequest()));
    }
}