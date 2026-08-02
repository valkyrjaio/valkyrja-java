/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.server.generator;

import io.valkyrja.http.message.response.contract.ResponseContract;
import io.valkyrja.http.server.generator.contract.ResponseFileGeneratorContract;
import io.valkyrja.support.generator.abstract_.FileGenerator;

public class ResponseFileGenerator extends FileGenerator implements ResponseFileGeneratorContract {

    protected ResponseContract response;

    public ResponseFileGenerator(ResponseContract response, String filePath) {
        super(filePath);
        this.response = response;
    }

    @Override
    public String generateFileContents() {
        StringBuilder sb = new StringBuilder();

        sb.append("statusCode=").append(response.getStatusCode().getValue()).append("\n");
        sb.append("reasonPhrase=").append(response.getReasonPhrase()).append("\n");
        sb.append("protocolVersion=").append(response.getProtocolVersion().getValue()).append("\n");

        response.getHeaders()
                .getAll()
                .forEach(
                        (name, header) ->
                                sb.append("header.")
                                        .append(name)
                                        .append("=")
                                        .append(header.getHeaderLine())
                                        .append("\n"));

        response.getBody().rewind();
        sb.append("body=").append(response.getBody().getContents()).append("\n");

        return sb.toString();
    }
}
