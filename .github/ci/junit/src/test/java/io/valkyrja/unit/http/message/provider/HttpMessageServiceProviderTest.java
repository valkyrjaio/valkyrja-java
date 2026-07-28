/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.http.message.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import io.valkyrja.container.manager.Container;
import io.valkyrja.http.message.provider.HttpMessageServiceProvider;
import io.valkyrja.http.message.response.factory.contract.ResponseFactoryContract;
import org.junit.jupiter.api.Test;

/** Test the {@link HttpMessageServiceProvider}. */
final class HttpMessageServiceProviderTest {

    @Test
    void publishersExposesResponseFactory() {
        assertEquals(1, new HttpMessageServiceProvider().publishers().size());
    }

    @Test
    void publishResponseFactoryBindsFactory() {
        var container = new Container();

        HttpMessageServiceProvider.publishResponseFactory(container);

        assertInstanceOf(
                ResponseFactoryContract.class,
                container.getSingleton(ResponseFactoryContract.class));
    }
}
