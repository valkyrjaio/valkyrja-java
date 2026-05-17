/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.request;

import io.valkyrja.http.message.abstract_.Message;
import io.valkyrja.http.message.constant.HeaderName;
import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.message.header.Header;
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.header.collection.contract.HeaderCollectionContract;
import io.valkyrja.http.message.request.contract.RequestContract;
import io.valkyrja.http.message.request.throwable.exception.HttpRequestInvalidRequestTargetException;
import io.valkyrja.http.message.stream.Stream;
import io.valkyrja.http.message.stream.contract.StreamContract;
import io.valkyrja.http.message.uri.Uri;
import io.valkyrja.http.message.uri.contract.UriContract;
import java.util.regex.Pattern;
import org.jspecify.annotations.Nullable;

public class Request extends Message implements RequestContract {

    private static final Pattern WHITESPACE = Pattern.compile("\\s");

    protected UriContract uri;
    protected RequestMethod method;
    protected @Nullable String requestTarget = null;

    public Request() {
        this(new Uri(), RequestMethod.GET, new Stream(), new HeaderCollection());
    }

    public Request(
            UriContract uri,
            RequestMethod method,
            StreamContract body,
            HeaderCollectionContract headers) {
        this.uri = uri;
        this.method = method;
        this.headers = headers;
        setBody(body);
        addHostHeaderFromUri();
    }

    @Override
    public String getRequestTarget() {
        if (requestTarget != null) {
            return requestTarget;
        }

        String target = uri.getPath();

        if (!uri.getQuery().isEmpty()) {
            target += "?" + uri.getQuery();
        }

        if (target.isEmpty()) {
            target = "/";
        }

        return target;
    }

    @Override
    public RequestContract withRequestTarget(String requestTarget) {
        validateRequestTarget(requestTarget);
        Request copy = (Request) copy();
        copy.requestTarget = requestTarget;
        return copy;
    }

    @Override
    public RequestMethod getMethod() {
        return method;
    }

    @Override
    public RequestContract withMethod(RequestMethod method) {
        Request copy = (Request) copy();
        copy.method = method;
        return copy;
    }

    @Override
    public UriContract getUri() {
        return uri;
    }

    @Override
    public RequestContract withUri(UriContract uri, boolean preserveHost) {
        Request copy = (Request) copy();
        copy.uri = uri;

        if (preserveHost && headers.has(HeaderName.HOST)) {
            return copy;
        }

        if (uri.getHost().isEmpty()) {
            return copy;
        }

        copy.headers = headers.withHeader(new Header(HeaderName.HOST, copy.getHostFromUri()));

        return copy;
    }

    @Override
    protected Request copy() {
        Request copy = new Request(uri, method, stream, headers);
        copy.requestTarget = requestTarget;
        copy.protocolVersion = protocolVersion;
        return copy;
    }

    protected void validateRequestTarget(String requestTarget) {
        if (WHITESPACE.matcher(requestTarget).find()) {
            throw new HttpRequestInvalidRequestTargetException(
                    "Invalid request target provided; cannot contain whitespace");
        }
    }

    protected String getHostFromUri() {
        String host = uri.getHost();
        int port = uri.getPort();
        return port != 0 ? host + ":" + port : host;
    }

    protected void addHostHeaderFromUri() {
        if (!headers.has(HeaderName.HOST) && !uri.getHost().isEmpty()) {
            headers = headers.withHeader(new Header(HeaderName.HOST, getHostFromUri()));
        }
    }
}
