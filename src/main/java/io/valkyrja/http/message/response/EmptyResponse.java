/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.response;

import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.header.collection.HeaderCollection;
import io.valkyrja.http.message.header.collection.contract.HeaderCollectionContract;
import io.valkyrja.http.message.response.contract.EmptyResponseContract;
import io.valkyrja.http.message.stream.Stream;
import io.valkyrja.http.message.stream.enum_.Mode;
import io.valkyrja.http.message.stream.enum_.ModeTranslation;
import io.valkyrja.http.message.stream.enum_.PhpWrapper;

public class EmptyResponse extends Response implements EmptyResponseContract {

    public EmptyResponse() {
        this(new HeaderCollection());
    }

    public EmptyResponse(HeaderCollectionContract headers) {
        super(
                new Stream(PhpWrapper.memory, Mode.READ, ModeTranslation.BINARY_SAFE),
                StatusCode.NO_CONTENT,
                headers);
    }
}
