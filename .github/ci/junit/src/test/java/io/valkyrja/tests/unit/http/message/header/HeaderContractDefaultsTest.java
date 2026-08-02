/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.message.header;

import static org.junit.jupiter.api.Assertions.assertThrows;

import io.valkyrja.http.message.header.contract.HeaderContract;
import io.valkyrja.http.message.header.value.component.contract.ComponentContract;
import io.valkyrja.http.message.header.value.contract.ValueContract;
import org.junit.jupiter.api.Test;

/** The contract-level {@code fromValue} statics must be overridden by concrete classes. */
final class HeaderContractDefaultsTest {

    @Test
    void headerContractFromValueIsUnsupported() {
        assertThrows(UnsupportedOperationException.class, () -> HeaderContract.fromValue("x"));
    }

    @Test
    void valueContractFromValueIsUnsupported() {
        assertThrows(UnsupportedOperationException.class, () -> ValueContract.fromValue("x"));
    }

    @Test
    void componentContractFromValueIsUnsupported() {
        assertThrows(UnsupportedOperationException.class, () -> ComponentContract.fromValue("x"));
    }
}
