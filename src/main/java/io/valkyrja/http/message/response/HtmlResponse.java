/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.response;

import io.valkyrja.http.message.constant.ContentTypeValue;
import io.valkyrja.http.message.constant.HeaderName;
import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.header.Header;
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.header.collection.contract.HeaderCollectionContract;
import io.valkyrja.http.message.response.contract.HtmlResponseContract;
import io.valkyrja.http.message.stream.Stream;

public class HtmlResponse extends Response implements HtmlResponseContract {

    public HtmlResponse() {
        this("", StatusCode.OK, new HeaderCollection());
    }

    public HtmlResponse(String html, StatusCode statusCode, HeaderCollectionContract headers) {
        super(createBody(html), statusCode, injectContentType(headers));
    }

    private static Stream createBody(String html) {
        Stream body = new Stream();
        body.write(html);
        body.rewind();
        return body;
    }

    private static HeaderCollectionContract injectContentType(HeaderCollectionContract headers) {
        return headers.withHeader(
                new Header(HeaderName.CONTENT_TYPE, ContentTypeValue.TEXT_HTML_UTF8));
    }
}
