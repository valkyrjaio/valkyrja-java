/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.valkyrja.http.message.constant.ContentTypeValue;
import io.valkyrja.http.message.constant.HeaderName;
import io.valkyrja.http.message.enum_.ProtocolVersion;
import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.message.file.collection.UploadedFileCollection;
import io.valkyrja.http.message.file.collection.contract.UploadedFileCollectionContract;
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.header.collection.contract.HeaderCollectionContract;
import io.valkyrja.http.message.param.CookieParamCollection;
import io.valkyrja.http.message.param.ParsedBodyParamCollection;
import io.valkyrja.http.message.param.ParsedJsonParamCollection;
import io.valkyrja.http.message.param.QueryParamCollection;
import io.valkyrja.http.message.param.ServerParamCollection;
import io.valkyrja.http.message.param.contract.CookieParamCollectionContract;
import io.valkyrja.http.message.param.contract.ParsedBodyParamCollectionContract;
import io.valkyrja.http.message.param.contract.ParsedJsonParamCollectionContract;
import io.valkyrja.http.message.param.contract.QueryParamCollectionContract;
import io.valkyrja.http.message.param.contract.ServerParamCollectionContract;
import io.valkyrja.http.message.request.contract.JsonServerRequestContract;
import io.valkyrja.http.message.stream.Stream;
import io.valkyrja.http.message.stream.contract.StreamContract;
import io.valkyrja.http.message.uri.Uri;
import io.valkyrja.http.message.uri.contract.UriContract;

import java.util.Map;

public class JsonServerRequest extends ServerRequest implements JsonServerRequestContract {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    protected boolean hadParsedBody = true;
    protected ParsedJsonParamCollectionContract parsedJson;

    public JsonServerRequest() {
        this(new Uri(), RequestMethod.GET, new Stream(), new HeaderCollection(), ProtocolVersion.V1_1,
                new ServerParamCollection(Map.of()), new CookieParamCollection(Map.of()),
                new QueryParamCollection(Map.of()), new ParsedBodyParamCollection(Map.of()),
                new ParsedJsonParamCollection(Map.of()), new UploadedFileCollection(Map.of()));
    }

    public JsonServerRequest(
            UriContract uri,
            RequestMethod method,
            StreamContract body,
            HeaderCollectionContract headers,
            ProtocolVersion protocol,
            ServerParamCollectionContract server,
            CookieParamCollectionContract cookies,
            QueryParamCollectionContract query,
            ParsedBodyParamCollectionContract parsedBody,
            ParsedJsonParamCollectionContract parsedJson,
            UploadedFileCollectionContract files
    ) {
        super(uri, method, body, headers, protocol, server, cookies, query, parsedBody, files, null);
        this.parsedJson = parsedJson;

        String contentType = headers.getHeaderLine(HeaderName.CONTENT_TYPE);

        if (contentType != null && contentType.contains(ContentTypeValue.APPLICATION_JSON)) {
            String bodyContents = stream.toString();
            if (!bodyContents.isEmpty()) {
                try {
                    Map<String, Object> jsonData = MAPPER.readValue(bodyContents, new TypeReference<Map<String, Object>>() {});
                    this.parsedJson = ParsedJsonParamCollection.fromArray(flattenToStringObjectMap(jsonData));
                } catch (Exception e) {
                    // malformed JSON — leave parsedJson as-is
                }
                if (parsedBody.getAll().isEmpty()) {
                    this.hadParsedBody = false;
                }
            }
        }
    }

    @Override
    public ParsedJsonParamCollectionContract getParsedJson() {
        return parsedJson;
    }

    @Override
    public JsonServerRequestContract withParsedJson(ParsedJsonParamCollectionContract params) {
        JsonServerRequest copy = (JsonServerRequest) copy();
        copy.parsedJson = params;
        return copy;
    }

    @Override
    protected JsonServerRequest copy() {
        JsonServerRequest copy = new JsonServerRequest(uri, method, stream, headers, protocolVersion,
                server, cookies, query, parsedBody, parsedJson, files);
        copy.requestTarget = requestTarget;
        copy.hadParsedBody = hadParsedBody;
        return copy;
    }

    private static Map<String, Object> flattenToStringObjectMap(Map<?, ?> source) {
        java.util.LinkedHashMap<String, Object> result = new java.util.LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : source.entrySet()) {
            result.put(String.valueOf(entry.getKey()), entry.getValue());
        }
        return result;
    }
}
