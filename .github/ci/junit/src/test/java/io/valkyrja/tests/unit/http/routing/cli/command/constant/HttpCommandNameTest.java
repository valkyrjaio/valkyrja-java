/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.routing.cli.command.constant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.valkyrja.http.routing.cli.command.constant.HttpCommandName;
import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;

/** Test the {@link HttpCommandName} constant holder. */
final class HttpCommandNameTest {

    @Test
    void exposesConstants() {
        assertEquals("http:list", HttpCommandName.LIST);
    }

    @Test
    void hasPrivateConstructor() throws Exception {
        Constructor<HttpCommandName> constructor = HttpCommandName.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertNotNull(constructor.newInstance());
    }
}
