/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.cli.interaction.message;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.valkyrja.cli.interaction.message.Header;
import io.valkyrja.cli.routing.data.contract.RouteContract;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Test the {@link Header} message. */
final class HeaderTest {

    private RouteContract route;

    @BeforeEach
    void setUp() {
        route = mock(RouteContract.class);
        when(route.getName()).thenReturn("list");
        when(route.getDescription()).thenReturn("List commands");
    }

    @Test
    void shortConstructorRendersAppRouteAndRuntimeInfo() {
        var text = new Header("MyApp", "2.0", route).getText();

        assertTrue(text.contains("MyApp v2.0"));
        assertTrue(text.contains("Built on Valkyrja v"));
        assertTrue(text.contains("Running on Java"));
        assertTrue(text.contains("List commands · list"));
    }

    @Test
    void fullConstructorUsesExplicitValuesAndMultiLineIcon() {
        var header =
                new Header(
                        "App",
                        "1.0",
                        route,
                        "ICON1\nICON2",
                        "9.9.9",
                        "2026-01-01",
                        "21",
                        "/project/root",
                        "Custom action",
                        "custom");

        var text = header.getText();
        assertTrue(text.contains("│   ICON1"));
        assertTrue(text.contains("│   ICON2"));
        assertTrue(text.contains("Built on Valkyrja v9.9.9 (date: 2026-01-01)"));
        assertTrue(text.contains("Running on Java 21"));
        assertTrue(text.contains("/project/root"));
        assertTrue(text.contains("Custom action · custom"));
    }

    @Test
    void withMethodsReturnUpdatedCopies() {
        var header = new Header("App", "1.0", route);

        assertTrue(header.withAppName("Renamed").getText().contains("Renamed v"));
        assertTrue(header.withAppVersion("3.3").getText().contains("v3.3"));
        assertTrue(header.withIcon("NEWICON").getText().contains("│   NEWICON"));
        assertTrue(header.withValkyrjaVersion("8.8.8").getText().contains("Valkyrja v8.8.8"));
        assertTrue(header.withValkyrjaBuildDate("date-x").getText().contains("date: date-x"));
        assertTrue(header.withJavaVersion("99").getText().contains("Running on Java 99"));
        assertTrue(header.withProjectRoot("/x/y").getText().contains("/x/y"));
        assertTrue(header.withActionDescription("Do it").getText().contains("Do it ·"));
        assertTrue(header.withCommandName("run").getText().contains("· run"));
        // original unchanged
        assertTrue(header.getText().contains("App v1.0"));
    }
}
