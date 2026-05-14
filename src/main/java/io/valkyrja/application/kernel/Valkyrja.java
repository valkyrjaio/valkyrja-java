/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.application.kernel;

import io.valkyrja.application.data.Config;
import io.valkyrja.application.data.contract.ConfigContract;
import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.application.provider.contract.ComponentProviderContract;
import io.valkyrja.cli.routing.provider.contract.CliRouteProviderContract;
import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import io.valkyrja.event.provider.contract.ListenerProviderContract;
import io.valkyrja.http.routing.provider.contract.HttpRouteProviderContract;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class Valkyrja implements ApplicationContract {

    protected final ContainerContract container;
    protected final ConfigContract config;

    protected List<ComponentProviderContract> providers = List.of();
    protected List<ServiceProviderContract> serviceProviders = List.of();
    protected List<ListenerProviderContract> eventProviders = List.of();
    protected List<CliRouteProviderContract> cliRouteProviders = List.of();
    protected List<HttpRouteProviderContract> httpRouteProviders = List.of();

    public Valkyrja(ContainerContract container) {
        this(container, new Config());
    }

    public Valkyrja(ContainerContract container, ConfigContract config) {
        this.container = container;
        this.config = config;
        bootstrapTimezone();
    }

    @Override
    public ContainerContract getContainer() {
        return container;
    }

    @Override
    public void publishProviderCallbacks() {
        for (var callback : config.callbacks()) {
            callback.accept(this);
        }
    }

    @Override
    public List<ComponentProviderContract> getProviders() {
        if (!providers.isEmpty()) {
            return providers;
        }

        List<ComponentProviderContract> result = new ArrayList<>();

        for (ComponentProviderContract provider : config.providers()) {
            result.addAll(provider.getComponentProviders(this));
            result.add(provider);
        }

        providers = result;
        return providers;
    }

    @Override
    public List<ServiceProviderContract> getContainerProviders() {
        if (!serviceProviders.isEmpty()) {
            return serviceProviders;
        }

        List<ServiceProviderContract> result = new ArrayList<>();

        for (ComponentProviderContract provider : getProviders()) {
            result.addAll(provider.getContainerProviders(this));
        }

        serviceProviders = result;
        return serviceProviders;
    }

    @Override
    public List<ListenerProviderContract> getEventProviders() {
        if (!eventProviders.isEmpty()) {
            return eventProviders;
        }

        List<ListenerProviderContract> result = new ArrayList<>();

        for (ComponentProviderContract provider : getProviders()) {
            result.addAll(provider.getEventProviders(this));
        }

        eventProviders = result;
        return eventProviders;
    }

    @Override
    public List<CliRouteProviderContract> getCliProviders() {
        if (!cliRouteProviders.isEmpty()) {
            return cliRouteProviders;
        }

        List<CliRouteProviderContract> result = new ArrayList<>();

        for (ComponentProviderContract provider : getProviders()) {
            result.addAll(provider.getCliProviders(this));
        }

        cliRouteProviders = result;
        return cliRouteProviders;
    }

    @Override
    public List<HttpRouteProviderContract> getHttpProviders() {
        if (!httpRouteProviders.isEmpty()) {
            return httpRouteProviders;
        }

        List<HttpRouteProviderContract> result = new ArrayList<>();

        for (ComponentProviderContract provider : getProviders()) {
            result.addAll(provider.getHttpProviders(this));
        }

        httpRouteProviders = result;
        return httpRouteProviders;
    }

    @Override
    public boolean getDebugMode() {
        return config.debugMode();
    }

    @Override
    public String getEnvironment() {
        return config.environment();
    }

    @Override
    public String getVersion() {
        return config.version();
    }

    protected void bootstrapTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone(config.timezone()));
    }
}
