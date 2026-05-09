/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.routing.attribute.route.requestmethod;

import io.valkyrja.http.routing.attribute.route.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RequestMethod(requestMethods = {
    io.valkyrja.http.message.enum_.RequestMethod.CONNECT,
    io.valkyrja.http.message.enum_.RequestMethod.DELETE,
    io.valkyrja.http.message.enum_.RequestMethod.GET,
    io.valkyrja.http.message.enum_.RequestMethod.HEAD,
    io.valkyrja.http.message.enum_.RequestMethod.OPTIONS,
    io.valkyrja.http.message.enum_.RequestMethod.PATCH,
    io.valkyrja.http.message.enum_.RequestMethod.POST,
    io.valkyrja.http.message.enum_.RequestMethod.PUT,
    io.valkyrja.http.message.enum_.RequestMethod.TRACE,
})
public @interface Any {
}