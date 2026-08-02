/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.client.manager.contract;

import io.valkyrja.http.message.request.contract.RequestContract;
import io.valkyrja.http.message.response.contract.ResponseContract;

public interface ClientContract {

    ResponseContract sendRequest(RequestContract request);
}
