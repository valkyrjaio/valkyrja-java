/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.message.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.response.JsonResponse;
import io.valkyrja.http.message.response.throwable.exception.HttpRequestInvalidJsonCallbackException;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Test the {@link JsonResponse}. */
final class JsonResponseTest {

    private static JsonResponse response() {
        return new JsonResponse(Map.of("key", "value"), StatusCode.OK, new HeaderCollection());
    }

    @Test
    void serializesDataToJsonBodyWithJsonContentType() {
        var response = response();

        assertTrue(response.getBody().toString().contains("\"key\":\"value\""));
        assertTrue(response.getHeaders().getHeaderLine("content-type").contains("json"));
    }

    @Test
    void createFromDataDefaults() {
        var response = JsonResponse.createFromData(null, null, null);

        assertEquals(StatusCode.OK, response.getStatusCode());
    }

    @Test
    void getBodyAsJsonRoundTrips() {
        assertEquals(Map.of("key", "value"), response().getBodyAsJson());
    }

    @Test
    void withJsonAsBodyReplacesBody() {
        var updated = response().withJsonAsBody(Map.of("a", "b"));

        assertTrue(((JsonResponse) updated).getBody().toString().contains("\"a\":\"b\""));
    }

    @Test
    void withCallbackWrapsBodyAsJsonp() {
        var jsonp = response().withCallback("myCallback");

        assertTrue(((JsonResponse) jsonp).getBody().toString().startsWith("/**/myCallback("));
        assertTrue(
                ((JsonResponse) jsonp)
                        .getHeaders()
                        .getHeaderLine("content-type")
                        .contains("javascript"));
    }

    @Test
    void withCallbackRejectsInvalidName() {
        assertThrows(
                HttpRequestInvalidJsonCallbackException.class,
                () -> response().withCallback("not valid!"));
    }

    @Test
    void withoutCallbackRestoresJsonBody() {
        var restored = response().withCallback("cb").withoutCallback();

        assertTrue(
                ((JsonResponse) restored)
                        .getHeaders()
                        .getHeaderLine("content-type")
                        .contains("json"));
    }

    @Test
    void defaultConstructorSerializesEmptyObject() {
        assertEquals("{}", new JsonResponse().getBody().toString());
    }

    @Test
    void getBodyAsJsonThrowsForInvalidJsonBody() {
        var body = new io.valkyrja.http.message.stream.Stream();
        body.write("not json");
        var response = (JsonResponse) new JsonResponse().withBody(body);

        assertThrows(RuntimeException.class, response::getBodyAsJson);
    }

    @Test
    void constructorThrowsWhenDataCannotBeSerialized() {
        // Jackson fails on an empty bean (no serializable properties) by default.
        assertThrows(
                RuntimeException.class,
                () ->
                        new JsonResponse(
                                Map.of("bad", new Object()),
                                StatusCode.OK,
                                new HeaderCollection()));
    }

    @Test
    void createFromDataWithProvidedValues() {
        var response =
                JsonResponse.createFromData(
                        Map.of("k", "v"), StatusCode.ACCEPTED, new HeaderCollection());

        assertEquals(StatusCode.ACCEPTED, response.getStatusCode());
    }
}
