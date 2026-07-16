/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.message.response;

import io.valkyrja.grpc.message.enum_.CancellationReason;
import io.valkyrja.grpc.message.metadata.Metadata;
import io.valkyrja.grpc.message.metadata.contract.MetadataContract;
import io.valkyrja.grpc.message.response.contract.ServiceResponseContract;
import io.valkyrja.grpc.message.status.Status;
import io.valkyrja.grpc.message.status.contract.StatusContract;
import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.Nullable;

/**
 * Immutable {@link ServiceResponseContract} implementation.
 *
 * <p>Built via the static factories ({@link #ok}, {@link #cancelled}, {@link #unimplemented}, …)
 * and refined with the {@code with*} copy methods. Messages are held as an {@link Iterable} of
 * agnostic {@link Object} payloads.
 */
public class ServiceResponse implements ServiceResponseContract {

    protected final StatusContract status;
    protected final MetadataContract initialMetadata;
    protected final MetadataContract trailingMetadata;
    protected final Iterable<Object> messages;

    public ServiceResponse(StatusContract status) {
        this(status, new Metadata(), new Metadata(), List.of());
    }

    public ServiceResponse(
            StatusContract status,
            MetadataContract initialMetadata,
            MetadataContract trailingMetadata,
            Iterable<Object> messages) {
        this.status = status;
        this.initialMetadata = initialMetadata;
        this.trailingMetadata = trailingMetadata;
        this.messages = messages;
    }

    @Override
    public StatusContract getStatus() {
        return status;
    }

    @Override
    public ServiceResponseContract withStatus(StatusContract status) {
        return new ServiceResponse(status, initialMetadata, trailingMetadata, messages);
    }

    @Override
    public MetadataContract getInitialMetadata() {
        return initialMetadata;
    }

    @Override
    public ServiceResponseContract withInitialMetadata(MetadataContract metadata) {
        return new ServiceResponse(status, metadata, trailingMetadata, messages);
    }

    @Override
    public MetadataContract getTrailingMetadata() {
        return trailingMetadata;
    }

    @Override
    public ServiceResponseContract withTrailingMetadata(MetadataContract metadata) {
        return new ServiceResponse(status, initialMetadata, metadata, messages);
    }

    @Override
    public Iterable<Object> getMessages() {
        return messages;
    }

    @Override
    public ServiceResponseContract withMessages(Iterable<Object> messages) {
        return new ServiceResponse(status, initialMetadata, trailingMetadata, messages);
    }

    @Override
    public boolean isCancellation() {
        return status.isCancellation();
    }

    // --- Factories -------------------------------------------------------------------------------

    public static ServiceResponse of(StatusContract status) {
        return new ServiceResponse(status);
    }

    public static ServiceResponse ok() {
        return new ServiceResponse(Status.ok());
    }

    public static ServiceResponse ok(Object message) {
        List<Object> messages = new ArrayList<>();
        messages.add(message);
        return new ServiceResponse(Status.ok(), new Metadata(), new Metadata(), messages);
    }

    public static ServiceResponse unimplemented() {
        return new ServiceResponse(Status.unimplemented(null));
    }

    public static ServiceResponse unimplemented(@Nullable String message) {
        return new ServiceResponse(Status.unimplemented(message));
    }

    public static ServiceResponse cancelled(@Nullable CancellationReason reason) {
        return new ServiceResponse(statusForReason(reason));
    }

    /**
     * Resolve the status for a cancellation reason: {@code DEADLINE_EXCEEDED} maps to that status,
     * everything else (including an unknown reason) maps to {@code CANCELLED}.
     *
     * @param reason the cancellation reason
     * @return the corresponding cancellation status
     */
    public static StatusContract statusForReason(@Nullable CancellationReason reason) {
        return reason == CancellationReason.DEADLINE_EXCEEDED
                ? Status.deadlineExceeded(null)
                : Status.cancelled(null);
    }
}
