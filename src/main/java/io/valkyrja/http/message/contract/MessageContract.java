/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
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
