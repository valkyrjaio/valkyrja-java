/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.grpc.message.status;

import io.valkyrja.grpc.message.enum_.StatusCode;
import io.valkyrja.grpc.message.status.contract.StatusContract;
import org.jspecify.annotations.Nullable;

/**
 * Immutable {@link StatusContract} implementation.
 *
 * <p>Instances are created via the per-code factory methods (e.g. {@link #ok()}, {@link
 * #notFound(String)}) or the {@code with*} copy methods. The message defaults from the code when
 * not supplied.
 */
public class Status implements StatusContract {

    protected final StatusCode code;
    protected final String message;
    protected final byte @Nullable [] details;

    public Status(StatusCode code) {
        this(code, code.getDefaultMessage(), null);
    }

    public Status(StatusCode code, String message) {
        this(code, message, null);
    }

    public Status(StatusCode code, String message, byte @Nullable [] details) {
        this.code = code;
        this.message = message;
        this.details = details == null ? null : details.clone();
    }

    @Override
    public StatusCode getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public byte @Nullable [] getDetails() {
        return details == null ? null : details.clone();
    }

    @Override
    public boolean hasDetails() {
        return details != null;
    }

    @Override
    public boolean isOk() {
        return code.isOk();
    }

    @Override
    public boolean isCancellation() {
        return code.isCancellation();
    }

    @Override
    public StatusContract withCode(StatusCode code) {
        return new Status(code, message, details);
    }

    @Override
    public StatusContract withMessage(String message) {
        return new Status(code, message, details);
    }

    @Override
    public StatusContract withDetails(byte @Nullable [] details) {
        return new Status(code, message, details);
    }

    // --- Factories -------------------------------------------------------------------------------

    public static Status of(StatusCode code) {
        return new Status(code);
    }

    public static Status of(StatusCode code, @Nullable String message) {
        return message == null ? new Status(code) : new Status(code, message);
    }

    public static Status ok() {
        return new Status(StatusCode.OK);
    }

    public static Status cancelled(@Nullable String message) {
        return of(StatusCode.CANCELLED, message);
    }

    public static Status unknown(@Nullable String message) {
        return of(StatusCode.UNKNOWN, message);
    }

    public static Status invalidArgument(@Nullable String message) {
        return of(StatusCode.INVALID_ARGUMENT, message);
    }

    public static Status deadlineExceeded(@Nullable String message) {
        return of(StatusCode.DEADLINE_EXCEEDED, message);
    }

    public static Status notFound(@Nullable String message) {
        return of(StatusCode.NOT_FOUND, message);
    }

    public static Status alreadyExists(@Nullable String message) {
        return of(StatusCode.ALREADY_EXISTS, message);
    }

    public static Status permissionDenied(@Nullable String message) {
        return of(StatusCode.PERMISSION_DENIED, message);
    }

    public static Status resourceExhausted(@Nullable String message) {
        return of(StatusCode.RESOURCE_EXHAUSTED, message);
    }

    public static Status failedPrecondition(@Nullable String message) {
        return of(StatusCode.FAILED_PRECONDITION, message);
    }

    public static Status aborted(@Nullable String message) {
        return of(StatusCode.ABORTED, message);
    }

    public static Status outOfRange(@Nullable String message) {
        return of(StatusCode.OUT_OF_RANGE, message);
    }

    public static Status unimplemented(@Nullable String message) {
        return of(StatusCode.UNIMPLEMENTED, message);
    }

    public static Status internal(@Nullable String message) {
        return of(StatusCode.INTERNAL, message);
    }

    public static Status internal(@Nullable String message, byte @Nullable [] details) {
        String resolved = message == null ? StatusCode.INTERNAL.getDefaultMessage() : message;
        return new Status(StatusCode.INTERNAL, resolved, details);
    }

    public static Status unavailable(@Nullable String message) {
        return of(StatusCode.UNAVAILABLE, message);
    }

    public static Status dataLoss(@Nullable String message) {
        return of(StatusCode.DATA_LOSS, message);
    }

    public static Status unauthenticated(@Nullable String message) {
        return of(StatusCode.UNAUTHENTICATED, message);
    }
}
