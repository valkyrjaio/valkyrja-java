/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.tests.unit.http.message.response;

import static org.junit.jupiter.api.Assertions.assertThrows;

import io.valkyrja.http.message.response.contract.JsonResponseContract;
import io.valkyrja.http.message.response.contract.RedirectResponseContract;
import io.valkyrja.http.message.response.contract.ResponseContract;
import org.junit.jupiter.api.Test;

/** The contract-level static factory methods must be overridden by concrete classes. */
final class ResponseContractDefaultsTest {

    @Test
    void responseContractCreateIsUnsupported() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> ResponseContract.create("x", null, null));
    }

    @Test
    void jsonResponseContractCreateIsUnsupported() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> JsonResponseContract.createFromData(null, null, null));
    }

    @Test
    void redirectResponseContractCreateIsUnsupported() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> RedirectResponseContract.createFromUri(null, null, null));
    }
}
