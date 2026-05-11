/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.message.contract;

import io.valkyrja.http.message.enum_.ProtocolVersion;
import io.valkyrja.http.message.header.collection.contract.HeaderCollectionContract;
import io.valkyrja.http.message.stream.contract.StreamContract;

public interface MessageContract {

    ProtocolVersion getProtocolVersion();

    MessageContract withProtocolVersion(ProtocolVersion version);

    HeaderCollectionContract getHeaders();

    MessageContract withHeaders(HeaderCollectionContract headers);

    StreamContract getBody();

    MessageContract withBody(StreamContract body);
}
