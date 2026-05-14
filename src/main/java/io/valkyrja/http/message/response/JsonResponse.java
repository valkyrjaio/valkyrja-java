/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.valkyrja.http.message.constant.ContentTypeValue;
import io.valkyrja.http.message.constant.HeaderName;
import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.header.ContentType;
import io.valkyrja.http.message.header.Header;
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.header.collection.contract.HeaderCollectionContract;
import io.valkyrja.http.message.response.contract.JsonResponseContract;
import io.valkyrja.http.message.response.throwable.exception.HttpRequestInvalidJsonCallbackException;
import io.valkyrja.http.message.stream.Stream;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.regex.Pattern;

public class JsonResponse extends Response implements JsonResponseContract {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Pattern JS_IDENTIFIER = Pattern.compile("^[$_\\p{L}][$_\\p{L}\\p{Mn}\\p{Mc}\\p{Nd}\\p{Pc}\\u200C\\u200D]*+$", Pattern.UNICODE_CHARACTER_CLASS);

    protected Map<String, Object> data;

    public JsonResponse() {
        this(Map.of(), StatusCode.OK, new HeaderCollection());
    }

    public JsonResponse(Map<String, Object> data, StatusCode statusCode, HeaderCollectionContract headers) {
        super(createBody(data), statusCode, headers.withHeader(new Header(HeaderName.CONTENT_TYPE, ContentTypeValue.APPLICATION_JSON)));
        this.data = data;
    }

    public static JsonResponse createFromData(@Nullable Map<String, Object> data, @Nullable StatusCode statusCode, @Nullable HeaderCollectionContract headers) {
        return new JsonResponse(
                data != null ? data : Map.of(),
                statusCode != null ? statusCode : StatusCode.OK,
                headers != null ? headers : new HeaderCollection()
        );
    }

    @Override
    public Map<String, Object> getBodyAsJson() {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = MAPPER.readValue(stream.getContents(), Map.class);
            stream.rewind();
            return result;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse JSON body", e);
        }
    }

    @Override
    public JsonResponseContract withJsonAsBody(Map<String, Object> data) {
        Stream newBody = createBody(data);
        return (JsonResponseContract) withBody(newBody);
    }

    @Override
    public JsonResponseContract withCallback(String callback) {
        verifyCallback(callback);
        JsonResponse copy = (JsonResponse) copy();
        copy.headers = headers.withHeader(new ContentType(ContentTypeValue.TEXT_JAVASCRIPT));
        String current = stream.getContents();
        stream.rewind();
        copy.stream = new Stream();
        copy.stream.write(String.format("/**/%s(%s);", callback, current));
        copy.stream.rewind();
        return copy;
    }

    @Override
    public JsonResponseContract withoutCallback() {
        JsonResponse copy = (JsonResponse) copy();
        copy.headers = headers.withHeader(new ContentType(ContentTypeValue.APPLICATION_JSON));
        copy.stream = createBody(data);
        return copy;
    }

    @Override
    protected Response copy() {
        JsonResponse copy = new JsonResponse(data, statusCode, headers);
        copy.statusPhrase = statusPhrase;
        copy.protocolVersion = protocolVersion;
        return copy;
    }

    private static Stream createBody(Map<String, Object> data) {
        try {
            Stream body = new Stream();
            body.write(MAPPER.writeValueAsString(data));
            body.rewind();
            return body;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize JSON", e);
        }
    }

    private void verifyCallback(String callback) {
        for (String part : callback.split("\\.")) {
            if (!JS_IDENTIFIER.matcher(part).matches()) {
                throw new HttpRequestInvalidJsonCallbackException("The callback name is not valid.");
            }
        }
    }
}
