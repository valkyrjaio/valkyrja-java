/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.fixtures.grpc;

import io.valkyrja.grpc.message.response.ServiceResponse;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.grpc.routing.attribute.Route;
import io.valkyrja.grpc.routing.attribute.Service;

/**
 * A route whose own method takes neither the container nor the route. The cached route passes it
 * both, where this cannot compile, so the collection rejects it.
 */
@Service(service = "pkg.WrongParameters")
public final class WrongParametersControllerFixture {

    @Route(name = "WrongParameters")
    public ServiceResponseContract wrongParameters() {
        return ServiceResponse.ok("method");
    }
}
