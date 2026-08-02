/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.server.middleware.inputreceived;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.valkyrja.cli.interaction.enum_.OptionType;
import io.valkyrja.cli.interaction.input.Input;
import io.valkyrja.cli.interaction.option.Option;
import io.valkyrja.cli.middleware.handler.contract.InputReceivedHandlerContract;
import io.valkyrja.cli.server.middleware.inputreceived.CheckForVersionOptionsMiddleware;
import org.junit.jupiter.api.Test;

/** Test the {@link CheckForVersionOptionsMiddleware}. */
final class CheckForVersionOptionsMiddlewareTest {

    @Test
    void rewritesToVersionCommandWhenOptionPresent() {
        var handler = mock(InputReceivedHandlerContract.class);
        when(handler.inputReceived(any())).thenAnswer(invocation -> invocation.getArgument(0));
        var middleware = new CheckForVersionOptionsMiddleware("version", "version", "V");

        assertNotNull(
                middleware.inputReceived(
                        new Input().withOptions(new Option("V", OptionType.SHORT)), handler));
        assertNotNull(middleware.inputReceived(new Input(), handler));
    }

    @Test
    void rewritesWhenLongOptionPresent() {
        var handler = mock(InputReceivedHandlerContract.class);
        when(handler.inputReceived(any())).thenAnswer(invocation -> invocation.getArgument(0));

        assertNotNull(
                new CheckForVersionOptionsMiddleware("version", "version", "V")
                        .inputReceived(
                                new Input().withOptions(new Option("version", OptionType.LONG)),
                                handler));
    }
}
