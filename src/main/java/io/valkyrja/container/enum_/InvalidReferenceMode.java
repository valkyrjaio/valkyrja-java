/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.container.enum_;

/** Mode for handling invalid service references in the container. */
public enum InvalidReferenceMode {

    /** Attempt to create a new instance of the class, or throw an exception if not possible. */
    NEW_INSTANCE_OR_THROW_EXCEPTION,

    /** Always throw an exception when the service is not found. */
    THROW_EXCEPTION,
}
