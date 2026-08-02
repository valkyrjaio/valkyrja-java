/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.request.contract;

import io.valkyrja.http.message.contract.MessageContract;
import io.valkyrja.http.message.enum_.RequestMethod;
import io.valkyrja.http.message.uri.contract.UriContract;

public interface RequestContract extends MessageContract {

    String getRequestTarget();

    RequestContract withRequestTarget(String requestTarget);

    RequestMethod getMethod();

    RequestContract withMethod(RequestMethod method);

    UriContract getUri();

    RequestContract withUri(UriContract uri, boolean preserveHost);
}
