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

public class XmlResponse extends Response implements HtmlResponseContract {

    public XmlResponse() {
        this("", StatusCode.OK, new HeaderCollection());
    }

    public XmlResponse(String xml, StatusCode statusCode, HeaderCollectionContract headers) {
        super(createBody(xml), statusCode, injectContentType(headers));
    }

    private static Stream createBody(String xml) {
        Stream body = new Stream();
        body.write(xml);
        body.rewind();
        return body;
    }

    private static HeaderCollectionContract injectContentType(HeaderCollectionContract headers) {
        return headers.withHeader(
                new Header(HeaderName.CONTENT_TYPE, ContentTypeValue.APPLICATION_XML_UTF8));
    }
}
