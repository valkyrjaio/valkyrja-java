/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.message.header.value.component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import io.valkyrja.http.message.header.value.component.Component;
import org.junit.jupiter.api.Test;

/** Test the {@link Component} header value component. */
final class ComponentTest {

    @Test
    void tokenOnlyConstructorHasEmptyText() {
        var component = new Component("token");

        assertEquals("token", component.getToken());
        assertEquals("", component.getText());
        assertEquals("token", component.toString());
    }

    @Test
    void tokenAndTextConstructor() {
        var component = new Component("key", "value");

        assertEquals("key", component.getToken());
        assertEquals("value", component.getText());
        assertEquals("key=value", component.toString());
        assertEquals("key=value", component.jsonSerialize());
    }

    @Test
    void fromValueWithEqualsSplitsTokenAndText() {
        var component = Component.fromValue("name = bob");

        assertEquals("name", component.getToken());
        assertEquals("bob", component.getText());
    }

    @Test
    void fromValueWithoutEqualsIsTokenOnly() {
        var component = Component.fromValue("flag");

        assertEquals("flag", component.getToken());
        assertEquals("", component.getText());
    }

    @Test
    void withMethodsReturnCopies() {
        var original = new Component("key", "value");

        var withToken = original.withToken("other");
        var withText = original.withText("changed");

        assertNotSame(original, withToken);
        assertEquals("other", withToken.getToken());
        assertEquals("changed", withText.getText());
        assertEquals("key", original.getToken());
    }

    @Test
    void toStringCoversTokenAndTextBranches() {
        assertEquals("key=value", new Component("key", "value").toString());
        assertEquals("token", new Component("token", "").toString());
        assertEquals("", new Component("", "value").toString());
    }
}
