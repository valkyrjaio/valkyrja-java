/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.request;

import io.valkyrja.http.message.constant.HeaderName;
import io.valkyrja.http.message.enum_.ProtocolVersion;
import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.message.file.collection.UploadedFileCollection;
import io.valkyrja.http.message.file.collection.contract.UploadedFileCollectionContract;
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.header.collection.contract.HeaderCollectionContract;
import io.valkyrja.http.message.param.AttributeParamCollection;
import io.valkyrja.http.message.param.CookieParamCollection;
import io.valkyrja.http.message.param.ParsedBodyParamCollection;
import io.valkyrja.http.message.param.QueryParamCollection;
import io.valkyrja.http.message.param.ServerParamCollection;
import io.valkyrja.http.message.param.contract.AttributeParamCollectionContract;
import io.valkyrja.http.message.param.contract.CookieParamCollectionContract;
import io.valkyrja.http.message.param.contract.ParsedBodyParamCollectionContract;
import io.valkyrja.http.message.param.contract.QueryParamCollectionContract;
import io.valkyrja.http.message.param.contract.ServerParamCollectionContract;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.stream.Stream;
import io.valkyrja.http.message.stream.contract.StreamContract;
import io.valkyrja.http.message.uri.Uri;
import io.valkyrja.http.message.uri.contract.UriContract;
import java.util.Map;
import org.jspecify.annotations.Nullable;

public class ServerRequest extends Request implements ServerRequestContract {

    protected ServerParamCollectionContract server;
    protected CookieParamCollectionContract cookies;
    protected QueryParamCollectionContract query;
    protected ParsedBodyParamCollectionContract parsedBody;
    protected UploadedFileCollectionContract files;
    protected AttributeParamCollectionContract attributes;

    public ServerRequest() {
        this(
                new Uri(),
                RequestMethod.GET,
                new Stream(),
                new HeaderCollection(),
                ProtocolVersion.V1_1,
                new ServerParamCollection(Map.of()),
                new CookieParamCollection(Map.of()),
                new QueryParamCollection(Map.of()),
                new ParsedBodyParamCollection(Map.of()),
                new UploadedFileCollection(Map.of()),
                new AttributeParamCollection());
    }

    public ServerRequest(
            UriContract uri,
            RequestMethod method,
            StreamContract body,
            HeaderCollectionContract headers,
            ProtocolVersion protocol,
            ServerParamCollectionContract server,
            CookieParamCollectionContract cookies,
            QueryParamCollectionContract query,
            ParsedBodyParamCollectionContract parsedBody,
            UploadedFileCollectionContract files,
            @Nullable AttributeParamCollectionContract attributes) {
        super(uri, method, body, headers);
        this.protocolVersion = protocol;
        this.server = server;
        this.cookies = cookies;
        this.query = query;
        this.parsedBody = parsedBody;
        this.files = files;
        this.attributes = attributes != null ? attributes : new AttributeParamCollection();
    }

    @Override
    public ServerParamCollectionContract getServerParams() {
        return server;
    }

    @Override
    public ServerRequestContract withServerParams(ServerParamCollectionContract server) {
        ServerRequest copy = (ServerRequest) copy();
        copy.server = server;
        return copy;
    }

    @Override
    public CookieParamCollectionContract getCookieParams() {
        return cookies;
    }

    @Override
    public ServerRequestContract withCookieParams(CookieParamCollectionContract cookies) {
        ServerRequest copy = (ServerRequest) copy();
        copy.cookies = cookies;
        return copy;
    }

    @Override
    public QueryParamCollectionContract getQueryParams() {
        return query;
    }

    @Override
    public ServerRequestContract withQueryParams(QueryParamCollectionContract query) {
        ServerRequest copy = (ServerRequest) copy();
        copy.query = query;
        return copy;
    }

    @Override
    public UploadedFileCollectionContract getUploadedFiles() {
        return files;
    }

    @Override
    public ServerRequestContract withUploadedFiles(UploadedFileCollectionContract uploadedFiles) {
        ServerRequest copy = (ServerRequest) copy();
        copy.files = uploadedFiles;
        return copy;
    }

    @Override
    public ParsedBodyParamCollectionContract getParsedBody() {
        return parsedBody;
    }

    @Override
    public ServerRequestContract withParsedBody(ParsedBodyParamCollectionContract params) {
        ServerRequest copy = (ServerRequest) copy();
        copy.parsedBody = params;
        return copy;
    }

    @Override
    public AttributeParamCollectionContract getAttributes() {
        return attributes;
    }

    @Override
    public ServerRequestContract withAttributes(AttributeParamCollectionContract attributes) {
        ServerRequest copy = (ServerRequest) copy();
        copy.attributes = attributes;
        return copy;
    }

    @Override
    public boolean isXmlHttpRequest() {
        return "XMLHttpRequest".equals(headers.getHeaderLine(HeaderName.X_REQUESTED_WITH));
    }

    @Override
    protected ServerRequest copy() {
        ServerRequest copy =
                new ServerRequest(
                        uri,
                        method,
                        stream,
                        headers,
                        protocolVersion,
                        server,
                        cookies,
                        query,
                        parsedBody,
                        files,
                        attributes);
        copy.requestTarget = requestTarget;
        return copy;
    }
}
