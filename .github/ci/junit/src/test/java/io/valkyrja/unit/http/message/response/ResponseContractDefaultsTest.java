/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.response;

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
                UnsupportedOperationException.class, () -> ResponseContract.create("x", null, null));
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