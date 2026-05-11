/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.response;

import io.valkyrja.http.message.abstract_.Message;
import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.header.SetCookie;
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.header.collection.contract.HeaderCollectionContract;
import io.valkyrja.http.message.header.value.contract.CookieContract;
import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.message.stream.Stream;
import io.valkyrja.http.message.stream.contract.StreamContract;

public class Response extends Message implements ResponseContract {

    protected StatusCode statusCode;
    protected String statusPhrase;

    public Response() {
        this(new Stream(), StatusCode.OK, new HeaderCollection());
    }

    public Response(StreamContract body, StatusCode statusCode, HeaderCollectionContract headers) {
        this.statusCode = statusCode;
        this.statusPhrase = statusCode.asPhrase();
        this.headers = headers;
        setBody(body);
    }

    public static Response create(String content, StatusCode statusCode, HeaderCollectionContract headers) {
        Stream stream = new Stream();
        stream.write(content != null ? content : "");
        stream.rewind();
        return new Response(stream, statusCode != null ? statusCode : StatusCode.OK, headers != null ? headers : new HeaderCollection());
    }

    @Override
    public StatusCode getStatusCode() {
        return statusCode;
    }

    @Override
    public ResponseContract withStatusCode(StatusCode code) {
        Response copy = (Response) copy();
        copy.statusCode = code;
        copy.statusPhrase = code.asPhrase();
        return copy;
    }

    @Override
    public String getReasonPhrase() {
        return statusPhrase;
    }

    @Override
    public ResponseContract withReasonPhrase(String reasonPhrase) {
        Response copy = (Response) copy();
        copy.statusPhrase = (reasonPhrase != null && !reasonPhrase.isEmpty()) ? reasonPhrase : statusCode.asPhrase();
        return copy;
    }

    @Override
    public ResponseContract withCookie(CookieContract cookie) {
        HeaderCollectionContract newHeaders = headers.withAddedHeaders(new SetCookie(cookie));
        return (ResponseContract) withHeaders(newHeaders);
    }

    @Override
    public ResponseContract withoutCookie(CookieContract cookie) {
        HeaderCollectionContract newHeaders = headers.withAddedHeaders(new SetCookie(cookie.delete()));
        return (ResponseContract) withHeaders(newHeaders);
    }

    @Override
    public ResponseContract sendHttpLine() {
        System.out.printf("HTTP/%s %d %s%n", protocolVersion.getValue(), statusCode.getValue(), statusPhrase.isEmpty() ? statusCode.asPhrase() : statusPhrase);
        return this;
    }

    @Override
    public ResponseContract sendHeaders() {
        for (var header : headers.getAll().values()) {
            System.out.println(header.toString());
        }
        return this;
    }

    @Override
    public ResponseContract sendBody() {
        if (stream.isSeekable()) {
            stream.rewind();
        }
        System.out.print(stream.getContents());
        stream.rewind();
        return this;
    }

    @Override
    public ResponseContract send() {
        sendHttpLine();
        sendHeaders();
        sendBody();
        return this;
    }

    @Override
    protected Response copy() {
        Response copy = new Response(stream, statusCode, headers);
        copy.statusPhrase = statusPhrase;
        copy.protocolVersion = protocolVersion;
        return copy;
    }
}
