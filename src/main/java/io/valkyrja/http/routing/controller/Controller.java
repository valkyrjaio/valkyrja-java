/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.routing.controller;

import io.valkyrja.http.message.request.contract.ServerRequestContract;
import io.valkyrja.http.message.response.factory.contract.ResponseFactoryContract;

public abstract class Controller {

    protected ServerRequestContract request;
    protected ResponseFactoryContract responseFactory;

    public Controller(ServerRequestContract request, ResponseFactoryContract responseFactory) {
        this.request = request;
        this.responseFactory = responseFactory;
    }
}
