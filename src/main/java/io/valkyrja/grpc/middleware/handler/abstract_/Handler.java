/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.grpc.middleware.handler.abstract_;

import io.valkyrja.container.manager.contract.ContainerContract;
import io.valkyrja.grpc.message.call.contract.ServiceCallContract;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.grpc.middleware.handler.contract.HandlerContract;
import io.valkyrja.grpc.support.Cancellation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jspecify.annotations.Nullable;

/**
 * The middleware-chain orchestrator base, shared by every stage handler.
 *
 * <p>Holds the ordered middleware for a stage and walks the chain: each call to {@link
 * #getMiddleware} resolves the next middleware from the container and advances the cursor, so a
 * middleware that returns without re-invoking its handler structurally short-circuits the
 * remainder.
 *
 * <p>The two-question cancellation check lives here in {@link #checkCancellation} so every
 * request-processing stage inherits it — the pre-check runs before delegating to the wrapped
 * middleware and the post-check on its return. The always-run stages ({@code SendingResponse},
 * {@code ResponseSent}) deliberately skip the check: per the fast-exit path they run even for
 * cancelled calls.
 */
public abstract class Handler<M> implements HandlerContract<M> {

    protected final ContainerContract container;
    protected final List<Class<? extends M>> middleware = new ArrayList<>();
    protected @Nullable Class<? extends M> next = null;
    protected int index = 0;

    @SafeVarargs
    protected Handler(ContainerContract container, Class<? extends M>... middleware) {
        this.container = container;
        this.middleware.addAll(Arrays.asList(middleware));
        updateNext();
    }

    @Override
    @SafeVarargs
    public final void add(Class<? extends M>... middleware) {
        this.middleware.addAll(Arrays.asList(middleware));
        updateNext();
    }

    @SuppressWarnings("unchecked")
    protected M getMiddleware(Class<? extends M> className) {
        M item = (M) container.get(className);
        index++;
        updateNext();
        return item;
    }

    protected void updateNext() {
        next = index < middleware.size() ? middleware.get(index) : null;
    }

    /**
     * Run the two-question cancellation check for a request-processing stage.
     *
     * @param call the current call
     * @param response the response in hand, or null if none exists yet
     * @return a cancellation response to fast-exit with, or null to continue normally
     */
    protected @Nullable ServiceResponseContract checkCancellation(
            ServiceCallContract call, @Nullable ServiceResponseContract response) {
        return Cancellation.checkAndFinalize(call, response);
    }
}
