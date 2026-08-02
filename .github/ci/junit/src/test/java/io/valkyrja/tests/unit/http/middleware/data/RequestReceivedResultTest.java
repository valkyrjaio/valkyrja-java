/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.middleware.data;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.EmptyResponse;
import io.valkyrja.http.middleware.data.RequestReceivedResult;
import org.junit.jupiter.api.Test;

/** Test the {@link RequestReceivedResult}. */
final class RequestReceivedResultTest {

    @Test
    void exposesRequestAndResponse() {
        var request = mock(ServerRequestContract.class);
        var response = new EmptyResponse();

        var result = new RequestReceivedResult(request, response);

        assertSame(request, result.request());
        assertSame(response, result.response());
        assertNull(new RequestReceivedResult(request, null).response());
    }
}
