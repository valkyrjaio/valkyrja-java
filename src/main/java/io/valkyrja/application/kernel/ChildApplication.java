/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.application.kernel;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.application.provider.contract.ComponentProviderContract;
import io.valkyrja.cli.routing.provider.contract.CliRouteProviderContract;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import io.valkyrja.event.provider.contract.ListenerProviderContract;
import io.valkyrja.http.routing.provider.contract.HttpRouteProviderContract;
import java.util.List;

/**
 * A per-request child application backed by a {@link io.valkyrja.container.manager.ChildContainer}
 * or {@link io.valkyrja.container.manager.NativeChildContainer}.
 *
 * <p>Owns a child container for request-scoped isolation. All non-container methods delegate to the
 * parent application — no re-bootstrapping occurs.
 *
 * <p>The parent application must be fully bootstrapped and its container frozen before any child is
 * created. Each request creates its own child for the lifetime of that request, then discards it.
 */
public class ChildApplication implements ApplicationContract {

    private final ApplicationContract parent;
    private final ContainerContract container;

    public ChildApplication(ApplicationContract parent, ContainerContract container) {
        this.parent = parent;
        this.container = container;
    }

    @Override
    public ContainerContract getContainer() {
        return container;
    }

    @Override
    public void publishProviderCallbacks() {
        parent.publishProviderCallbacks();
    }

    @Override
    public List<ComponentProviderContract> getProviders() {
        return parent.getProviders();
    }

    @Override
    public List<ServiceProviderContract> getContainerProviders() {
        return parent.getContainerProviders();
    }

    @Override
    public List<ListenerProviderContract> getEventProviders() {
        return parent.getEventProviders();
    }

    @Override
    public List<CliRouteProviderContract> getCliProviders() {
        return parent.getCliProviders();
    }

    @Override
    public List<HttpRouteProviderContract> getHttpProviders() {
        return parent.getHttpProviders();
    }

    @Override
    public boolean getDebugMode() {
        return parent.getDebugMode();
    }

    @Override
    public String getEnvironment() {
        return parent.getEnvironment();
    }

    @Override
    public String getVersion() {
        return parent.getVersion();
    }
}
