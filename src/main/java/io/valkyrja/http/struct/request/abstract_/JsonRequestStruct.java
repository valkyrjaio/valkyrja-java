/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.struct.request.abstract_;

import io.valkyrja.http.message.request.contract.JsonServerRequestContract;
import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.struct.throwable.exception.HttpStructJsonServerRequestExpectedException;
import java.util.Map;

public abstract class JsonRequestStruct extends RequestStruct {

    @Override
    protected Map<String, Object> getOnlyParamsFromRequest(
            ServerRequestContract request, String... values) {
        ensureJsonRequest(request);
        return ((JsonServerRequestContract) request).getParsedJson().getOnly(values);
    }

    @Override
    protected Map<String, Object> getExceptParamsFromRequest(
            ServerRequestContract request, String... values) {
        ensureJsonRequest(request);
        return ((JsonServerRequestContract) request).getParsedJson().getAllExcept(values);
    }

    protected void ensureJsonRequest(ServerRequestContract request) {
        if (!(request instanceof JsonServerRequestContract)) {
            throw new HttpStructJsonServerRequestExpectedException(
                    "JsonServerRequest is required for this to work.");
        }
    }
}
