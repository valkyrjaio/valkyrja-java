/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.routing.data.contract;

import io.valkyrja.type.data.Cast;
import org.jspecify.annotations.Nullable;

public interface ParameterContract {

    String getName();

    ParameterContract withName(String name);

    String getRegex();

    ParameterContract withRegex(String regex);

    boolean hasCast();

    Cast getCast();

    ParameterContract withCast(Cast cast);

    boolean isOptional();

    ParameterContract withIsOptional(boolean isOptional);

    boolean shouldCapture();

    ParameterContract withShouldCapture(boolean shouldCapture);

    @Nullable Object getDefault();

    ParameterContract withDefault(@Nullable Object defaultValue);

    @Nullable Object getValue();

    ParameterContract withValue(@Nullable Object value);
}
