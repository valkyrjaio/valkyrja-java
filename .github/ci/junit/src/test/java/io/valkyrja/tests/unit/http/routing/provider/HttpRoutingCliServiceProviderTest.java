/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.routing.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;

import io.valkyrja.cli.interaction.output.factory.contract.OutputFactoryContract;
import io.valkyrja.container.manager.Container;
import io.valkyrja.http.routing.cli.command.ListCommand;
import io.valkyrja.http.routing.collection.RouteCollection;
import io.valkyrja.http.routing.collection.contract.RouteCollectionContract;
import io.valkyrja.http.routing.provider.HttpRoutingCliServiceProvider;
import org.junit.jupiter.api.Test;

/** Test the {@link HttpRoutingCliServiceProvider}. */
final class HttpRoutingCliServiceProviderTest {

    @Test
    void publishersExposeListCommand() {
        assertEquals(1, new HttpRoutingCliServiceProvider().publishers().size());
    }

    @Test
    void publishListCommandBindsCommand() {
        var container = new Container();
        container.setSingleton(RouteCollectionContract.class, new RouteCollection());
        container.setSingleton(OutputFactoryContract.class, mock(OutputFactoryContract.class));

        HttpRoutingCliServiceProvider.publishListCommand(container);

        assertInstanceOf(ListCommand.class, container.getSingleton(ListCommand.class));
    }
}
