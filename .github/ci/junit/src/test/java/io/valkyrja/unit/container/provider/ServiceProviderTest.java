/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.unit.container.provider;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.data.contract.ContainerDataContract;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.provider.*;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import java.util.List;
import org.junit.jupiter.api.Test;

final class ServiceProviderTest {

    @Test
    void publishersContainsContainerData() {
        assertTrue(new ServiceProvider().publishers().containsKey(ContainerDataContract.class));
    }

    @Test
    void publishDataRegistersProvidersAndStoresData() {
        var container = mock(ContainerContract.class);
        var app = mock(ApplicationContract.class);
        var nestedProvider = mock(ServiceProviderContract.class);
        var data = mock(ContainerDataContract.class);

        when(container.getSingleton(ApplicationContract.class)).thenReturn(app);
        when(app.getContainerProviders()).thenReturn(List.of(nestedProvider));
        when(container.getData()).thenReturn(data);

        ServiceProvider.publishData(container);

        verify(container).register(nestedProvider);
        verify(container).setSingleton(ContainerDataContract.class, data);
    }
}
