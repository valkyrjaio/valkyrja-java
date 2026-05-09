/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
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
        return headers.withHeader(new Header(HeaderName.CONTENT_TYPE, ContentTypeValue.APPLICATION_XML_UTF8));
    }
}