/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.container.provider;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.container.data.contract.ContainerDataContract;
import io.valkyrja.container.manager.contract.ContainerContract;
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
