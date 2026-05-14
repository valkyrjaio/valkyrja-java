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
import io.valkyrja.http.message.response.contract.TextResponseContract;
import io.valkyrja.http.message.stream.Stream;
import org.jspecify.annotations.Nullable;

public class TextResponse extends Response implements TextResponseContract {

    public TextResponse() {
        this("", StatusCode.OK, new HeaderCollection());
    }

    public TextResponse(String text, StatusCode statusCode, HeaderCollectionContract headers) {
        super(createBody(text), statusCode, injectContentType(headers));
    }

    public static TextResponse create(@Nullable String content, @Nullable StatusCode statusCode, @Nullable HeaderCollectionContract headers) {
        return new TextResponse(
                content != null ? content : "",
                statusCode != null ? statusCode : StatusCode.OK,
                headers != null ? headers : new HeaderCollection()
        );
    }

    private static Stream createBody(String text) {
        Stream body = new Stream();
        body.write(text);
        body.rewind();
        return body;
    }

    private static HeaderCollectionContract injectContentType(HeaderCollectionContract headers) {
        return headers.withHeader(new Header(HeaderName.CONTENT_TYPE, ContentTypeValue.TEXT_PLAIN_UTF8));
    }
}
