/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.response;

import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.header.Location;
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.header.collection.contract.HeaderCollectionContract;
import io.valkyrja.http.message.header.contract.HeaderContract;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.contract.RedirectResponseContract;
import io.valkyrja.http.message.response.throwable.exception.HttpRequestInvalidRedirectStatusCodeException;
import org.jspecify.annotations.Nullable;
import io.valkyrja.http.message.uri.Uri;
import io.valkyrja.http.message.uri.contract.UriContract;
import io.valkyrja.http.message.uri.enum_.Scheme;
import io.valkyrja.http.message.uri.factory.UriFactory;

public class RedirectResponse extends Response implements RedirectResponseContract {

    protected UriContract uri;

    public RedirectResponse() {
        this(new Uri("/"), StatusCode.FOUND, new HeaderCollection());
    }

    public RedirectResponse(UriContract uri, StatusCode statusCode, HeaderCollectionContract headers) {
        super(null, validateRedirectStatus(statusCode), headers.withHeader(buildLocationHeader(uri)));
        this.uri = uri;
    }

    public static RedirectResponse createFromUri(@Nullable UriContract uri, @Nullable StatusCode statusCode, @Nullable HeaderCollectionContract headers) {
        UriContract resolvedUri = uri != null ? uri : new Uri("/");
        return new RedirectResponse(
                resolvedUri,
                statusCode != null ? statusCode : StatusCode.FOUND,
                headers != null ? headers : new HeaderCollection()
        );
    }

    @Override
    public UriContract getUri() {
        return uri;
    }

    @Override
    public RedirectResponseContract withUri(UriContract uri) {
        RedirectResponse copy = (RedirectResponse) copy();
        copy.uri = uri;
        copy.headers = headers.withHeader(buildLocationHeader(uri));
        return copy;
    }

    @Override
    public RedirectResponseContract secure(String path, ServerRequestContract request) {
        UriContract secureUri = new Uri(Scheme.HTTPS, "", "", request.getUri().getHostPort(), 0, path, "", "");
        return withUri(secureUri);
    }

    @Override
    public RedirectResponseContract back(ServerRequestContract request) {
        String refererLine = request.getHeaders().getHeaderLine("Referer");
        if (refererLine == null || refererLine.isEmpty()) {
            refererLine = "/";
        }
        UriContract refererUri = UriFactory.fromString(refererLine);
        UriContract finalUri = isInternalUri(request, refererUri) ? refererUri : new Uri("/");
        return withUri(finalUri);
    }

    @Override
    protected Response copy() {
        RedirectResponse copy = new RedirectResponse(uri, statusCode, headers);
        copy.statusPhrase = statusPhrase;
        copy.protocolVersion = protocolVersion;
        return copy;
    }

    private boolean isInternalUri(ServerRequestContract request, UriContract uri) {
        String host = uri.getHost();
        return host == null || host.isEmpty() || host.equals(request.getUri().getHost());
    }

    private static HeaderContract buildLocationHeader(UriContract uri) {
        String uriString = uri.toString();
        if (uriString.isEmpty()) {
            uriString = "/";
        }
        return new Location(uriString);
    }

    private static StatusCode validateRedirectStatus(StatusCode statusCode) {
        if (!statusCode.isRedirect()) {
            throw new HttpRequestInvalidRedirectStatusCodeException("Invalid redirect status code " + statusCode.getValue() + " used.");
        }
        return statusCode;
    }
}
