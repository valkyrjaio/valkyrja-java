/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.application.provider.contract;

import io.valkyrja.application.kernel.contract.ApplicationContract;
import io.valkyrja.cli.routing.provider.contract.CliRouteProviderContract;
import io.valkyrja.container.provider.contract.ServiceProviderContract;
import io.valkyrja.event.provider.contract.ListenerProviderContract;
import io.valkyrja.grpc.routing.provider.contract.GrpcRouteProviderContract;
import io.valkyrja.http.routing.provider.contract.HttpRouteProviderContract;
import java.util.List;

/**
 * Contract for the top-level provider aggregator of a component.
 *
 * <p>Every method here is abstract, so no protocol is privileged over another. An implementation
 * declares each method, including a method that returns an empty list. A reader of one provider
 * then sees what the component supplies, and what it does not supply, from that one file.
 *
 * <p>Warning: a base class that supplies empty defaults breaks both properties. Do not add one. An
 * inherited default is not a method on the component, so no test asserts it, and a component that
 * must supply a provider can return empty with nothing to report the fault.
 */
public interface ComponentProviderContract {

    /**
     * Get the component providers this component depends on.
     *
     * @param app the application
     * @return list of component provider instances
     */
    List<ComponentProviderContract> getComponentProviders(ApplicationContract app);

    /**
     * Get the component's container service providers.
     *
     * @param app the application
     * @return list of container provider instances
     */
    List<ServiceProviderContract> getContainerProviders(ApplicationContract app);

    /**
     * Get the component's event listener providers.
     *
     * @param app the application
     * @return list of event provider instances
     */
    List<ListenerProviderContract> getEventProviders(ApplicationContract app);

    /**
     * Get the component's CLI route providers.
     *
     * @param app the application
     * @return list of CLI route provider instances
     */
    List<CliRouteProviderContract> getCliProviders(ApplicationContract app);

    /**
     * Get the component's HTTP route providers.
     *
     * @param app the application
     * @return list of HTTP route provider instances
     */
    List<HttpRouteProviderContract> getHttpProviders(ApplicationContract app);

    /**
     * Get the component's gRPC route providers.
     *
     * @param app the application
     * @return list of gRPC route provider instances
     */
    List<GrpcRouteProviderContract> getGrpcProviders(ApplicationContract app);
}
