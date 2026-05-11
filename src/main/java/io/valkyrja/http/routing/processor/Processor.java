/*
 * This file is part of the Valkyrja Framework package.
 *
 * (c) Melech Mizrachi <melechmizrachi@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package io.valkyrja.http.routing.processor;

import io.valkyrja.http.routing.constant.Regex;
import io.valkyrja.http.routing.data.contract.DynamicRouteContract;
import io.valkyrja.http.routing.data.contract.ParameterContract;
import io.valkyrja.http.routing.data.contract.RouteContract;
import io.valkyrja.http.routing.processor.contract.ProcessorContract;
import io.valkyrja.http.routing.throwable.exception.HttpRoutingInvalidRoutePathException;

public class Processor implements ProcessorContract {

    @Override
    public RouteContract route(RouteContract route) {
        String path = "/" + route.getPath().replaceAll("^/+|/+$", "");
        route = route.withPath(path);

        if (route instanceof DynamicRouteContract dynamic && dynamic.getPath().contains("{")) {
            return modifyRegex(dynamic);
        }

        return route;
    }

    protected RouteContract modifyRegex(DynamicRouteContract route) {
        if (!route.getRegex().isEmpty()) {
            return route;
        }

        String regex = route.getPath().replace("/", Regex.PATH);

        for (ParameterContract parameter : route.getParameters()) {
            parameter = processParameterInRegex(parameter, regex);
            regex = replaceParameterNameInRegex(route, parameter, regex);
        }

        regex = Regex.START + regex + Regex.END;

        return route.withRegex(regex);
    }

    protected ParameterContract processParameterInRegex(ParameterContract parameter, String regex) {
        if (parameter.isOptional() || regex.contains(parameter.getName() + "?")) {
            return parameter.withIsOptional(true);
        }
        return parameter;
    }

    protected String replaceParameterNameInRegex(RouteContract route, ParameterContract parameter, String regex) {
        String nameReplacement = getRegexParameterNameReplacement(parameter);

        if (!regex.contains(nameReplacement)) {
            throw new HttpRoutingInvalidRoutePathException(route.getPath() + " is missing " + nameReplacement);
        }

        String parameterRegex = getParameterRegex(parameter);

        return regex.replace(nameReplacement, parameterRegex);
    }

    protected String getRegexParameterNameReplacement(ParameterContract parameter) {
        boolean isOptional = parameter.isOptional();
        return (isOptional ? Regex.PATH : "")
            + "{" + parameter.getName() + (isOptional ? "?" : "") + "}";
    }

    protected String getParameterRegex(ParameterContract parameter) {
        return getParameterRegexOptionalCaptureGroupStart(parameter)
            + getParameterRegexCaptureGroupStart(parameter)
            + getParameterRegexNameCaptureGroup(parameter)
            + parameter.getRegex()
            + getParameterRegexCaptureGroupEnd(parameter);
    }

    protected String getParameterRegexOptionalCaptureGroupStart(ParameterContract parameter) {
        return parameter.isOptional() ? Regex.START_OPTIONAL_CAPTURE_GROUP : "";
    }

    protected String getParameterRegexCaptureGroupStart(ParameterContract parameter) {
        return parameter.shouldCapture() ? Regex.START_CAPTURE_GROUP : Regex.START_NON_CAPTURE_GROUP;
    }

    protected String getParameterRegexNameCaptureGroup(ParameterContract parameter) {
        return parameter.shouldCapture()
            ? Regex.START_CAPTURE_GROUP_NAME + parameter.getName() + Regex.END_CAPTURE_GROUP_NAME
            : "";
    }

    protected String getParameterRegexCaptureGroupEnd(ParameterContract parameter) {
        return parameter.isOptional() ? Regex.END_OPTIONAL_CAPTURE_GROUP : Regex.END_CAPTURE_GROUP;
    }
}
