/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
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
