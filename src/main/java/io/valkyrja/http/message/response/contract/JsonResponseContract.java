/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.response.contract;

import io.valkyrja.http.message.enum_.StatusCode;
import io.valkyrja.http.message.header.collection.contract.HeaderCollectionContract;
import java.util.Map;

public interface JsonResponseContract extends ResponseContract {

    static JsonResponseContract createFromData(
            Map<String, Object> data, StatusCode statusCode, HeaderCollectionContract headers) {
        throw new UnsupportedOperationException(
                "createFromData must be implemented by the concrete class");
    }

    Map<String, Object> getBodyAsJson();

    JsonResponseContract withJsonAsBody(Map<String, Object> data);

    JsonResponseContract withCallback(String callback);

    JsonResponseContract withoutCallback();
}
