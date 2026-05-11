/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.abstract_;

import io.valkyrja.http.message.contract.MessageContract;
import io.valkyrja.http.message.enum_.ProtocolVersion;
import io.valkyrja.http.message.header.collection.contract.HeaderCollectionContract;
import io.valkyrja.http.message.header.contract.HeaderContract;
import io.valkyrja.http.message.stream.contract.StreamContract;

public abstract class Message implements MessageContract {

    protected HeaderCollectionContract headers;
    protected ProtocolVersion protocolVersion = ProtocolVersion.V1_1;
    protected StreamContract stream;

    @Override
    public ProtocolVersion getProtocolVersion() {
        return protocolVersion;
    }

    @Override
    public MessageContract withProtocolVersion(ProtocolVersion version) {
        Message copy = copy();
        copy.protocolVersion = version;
        return copy;
    }

    @Override
    public HeaderCollectionContract getHeaders() {
        return headers;
    }

    @Override
    public MessageContract withHeaders(HeaderCollectionContract headers) {
        Message copy = copy();
        copy.headers = headers;
        return copy;
    }

    @Override
    public StreamContract getBody() {
        return stream;
    }

    @Override
    public MessageContract withBody(StreamContract body) {
        Message copy = copy();
        copy.setBody(body);
        body.rewind();
        return copy;
    }

    protected void setBody(StreamContract body) {
        this.stream = body;
    }

    protected HeaderCollectionContract injectHeader(HeaderContract header, HeaderCollectionContract headers, boolean override) {
        String headerName = header.getNormalizedName();
        HeaderContract newHeader = (override || !headers.has(headerName))
                ? header
                : headers.get(headerName).withAddedValues(header.getValues().toArray());
        return headers.withHeader(newHeader);
    }

    protected abstract Message copy();
}
