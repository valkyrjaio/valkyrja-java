/*
 * This file is part of the Valkyrja Framework package.
 *
 * Copyright (c) 2016-present Melech Mizrachi
 *
 * Released under the MIT License. See LICENSE.md for details.
 */

package io.valkyrja.http.message.request.factory;

import io.valkyrja.http.message.constant.ContentTypeValue;
import io.valkyrja.http.message.file.contract.UploadedFileContract;
import io.valkyrja.http.message.file.factory.UploadedFileFactory;
import io.valkyrja.http.message.request.data.ParsedRequestBody;
import io.valkyrja.http.message.stream.Stream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jspecify.annotations.Nullable;

/**
 * Splits a raw request body into parsed form fields and uploaded files, keyed off the request's
 * {@code Content-Type}.
 *
 * <p>This is the raw-runtime analog of the parsing PHP delegates to its SAPI ({@code $_POST} /
 * {@code $_FILES}): the JDK {@link com.sun.net.httpserver.HttpServer HttpServer}, Netty, and the
 * servlet runtimes hand over only the raw body, so the framework parses it here.
 *
 * <p>Supported content types:
 *
 * <ul>
 *   <li>{@code application/x-www-form-urlencoded} — decoded into parsed-body fields.
 *   <li>{@code multipart/form-data} — each part becomes a field (no filename) or an uploaded file
 *       (with a filename).
 * </ul>
 *
 * <p>Any other content type (JSON, plain text, etc.) yields no parsed fields or files; that body is
 * available as the request's raw body stream (JSON is additionally parsed by {@link
 * io.valkyrja.http.message.request.JsonServerRequest}).
 *
 * <p>Bodies flow through the framework's UTF-8 {@link Stream}, so file parts carrying non-UTF-8
 * binary content are subject to that stream's text encoding — full binary fidelity would require a
 * byte-oriented body stream.
 */
public abstract class RequestBodyFactory {

    private static final Pattern BOUNDARY = Pattern.compile("boundary=\"?([^\";]+)\"?");
    private static final Pattern NAME = Pattern.compile("name=\"([^\"]*)\"");
    private static final Pattern FILENAME = Pattern.compile("filename=\"([^\"]*)\"");

    /**
     * Parse a raw request body into its form fields and uploaded files.
     *
     * @param contentType the request's {@code Content-Type} header line, or {@code null}
     * @param body the raw request body, or {@code null}
     * @return the parsed fields and files (both empty when nothing is parseable)
     */
    public static ParsedRequestBody parse(@Nullable String contentType, @Nullable String body) {
        String type = contentType != null ? contentType : "";
        String content = body != null ? body : "";

        if (type.contains(ContentTypeValue.MULTIPART_FORM_DATA)) {
            String boundary = extractBoundary(type);

            if (boundary == null || content.isEmpty()) {
                return new ParsedRequestBody(Map.of(), Map.of());
            }

            return parseMultipart(content, boundary);
        }

        if (type.contains(ContentTypeValue.APPLICATION_X_WWW_FORM)) {
            return new ParsedRequestBody(parseUrlEncoded(content), Map.of());
        }

        return new ParsedRequestBody(Map.of(), Map.of());
    }

    /**
     * Parse an {@code application/x-www-form-urlencoded} string (a form body or a URL query string)
     * into a map.
     *
     * <p>Splits on {@code &}, decodes each {@code key=value} pair (percent- and {@code +}-decoded),
     * treats a pair with no {@code =} as an empty value, and tolerates a leading {@code ?} and
     * empty pairs.
     *
     * @param input the encoded string (with or without a leading {@code ?}), or {@code null}
     * @return the decoded params (empty when the input is absent or blank)
     */
    public static Map<String, Object> parseUrlEncoded(@Nullable String input) {
        Map<String, Object> params = new LinkedHashMap<>();

        if (input == null || input.isEmpty()) {
            return params;
        }

        String value = input.charAt(0) == '?' ? input.substring(1) : input;

        for (String pair : value.split("&")) {
            if (pair.isEmpty()) {
                continue;
            }

            int equals = pair.indexOf('=');
            String key = equals >= 0 ? pair.substring(0, equals) : pair;
            String pairValue = equals >= 0 ? pair.substring(equals + 1) : "";

            params.put(
                    URLDecoder.decode(key, StandardCharsets.UTF_8),
                    URLDecoder.decode(pairValue, StandardCharsets.UTF_8));
        }

        return params;
    }

    /**
     * Extract the boundary token from a {@code multipart/form-data} content-type line.
     *
     * @param contentType the content-type line
     * @return the boundary, or {@code null} if none is present
     */
    protected static @Nullable String extractBoundary(String contentType) {
        Matcher matcher = BOUNDARY.matcher(contentType);

        return matcher.find() ? matcher.group(1).trim() : null;
    }

    /**
     * Parse a {@code multipart/form-data} body into fields and uploaded files.
     *
     * @param body the raw multipart body
     * @param boundary the boundary token
     * @return the parsed fields and files
     */
    protected static ParsedRequestBody parseMultipart(String body, String boundary) {
        Map<String, Object> fields = new LinkedHashMap<>();
        Map<String, UploadedFileContract> files = new LinkedHashMap<>();

        String delimiter = "--" + boundary;

        for (String segment : body.split(Pattern.quote(delimiter))) {
            // Skip the preamble (empty leading segment) and the closing delimiter's "--" tail.
            if (segment.isEmpty() || segment.startsWith("--")) {
                continue;
            }

            String part = stripTrailingNewline(stripLeadingNewline(segment));

            int separator = part.indexOf("\r\n\r\n");
            int separatorLength = 4;

            if (separator < 0) {
                separator = part.indexOf("\n\n");
                separatorLength = 2;
            }

            if (separator < 0) {
                continue;
            }

            String headerBlock = part.substring(0, separator);
            String content = part.substring(separator + separatorLength);

            addPart(fields, files, headerBlock, content);
        }

        return new ParsedRequestBody(fields, files);
    }

    /**
     * Add a single parsed part to either the fields or the files, based on its headers.
     *
     * @param fields the accumulating field map
     * @param files the accumulating file map
     * @param headerBlock the part's raw header block
     * @param content the part's raw content
     */
    protected static void addPart(
            Map<String, Object> fields,
            Map<String, UploadedFileContract> files,
            String headerBlock,
            String content) {
        String disposition = "";
        String partContentType = "";

        for (String line : headerBlock.split("\r\n|\n")) {
            String lower = line.toLowerCase(Locale.ROOT);

            if (lower.startsWith("content-disposition:")) {
                disposition = line;
            } else if (lower.startsWith("content-type:")) {
                partContentType = line.substring(line.indexOf(':') + 1).trim();
            }
        }

        String name = firstGroup(NAME, disposition);

        if (name == null) {
            return;
        }

        String filename = firstGroup(FILENAME, disposition);

        if (filename == null) {
            fields.put(name, content);

            return;
        }

        Stream stream = new Stream();
        stream.write(content);
        stream.rewind();

        files.put(
                name,
                UploadedFileFactory.createFromStream(
                        stream,
                        content.getBytes(StandardCharsets.UTF_8).length,
                        filename,
                        partContentType));
    }

    /**
     * Return the first capturing group of {@code pattern} in {@code input}, or {@code null}.
     *
     * @param pattern the pattern to match
     * @param input the string to search
     * @return the first group, or {@code null} if the pattern does not match
     */
    protected static @Nullable String firstGroup(Pattern pattern, String input) {
        Matcher matcher = pattern.matcher(input);

        return matcher.find() ? matcher.group(1) : null;
    }

    /**
     * Strip a single leading {@code \r\n} or {@code \n} from a multipart segment.
     *
     * @param segment the segment
     * @return the segment without its leading newline
     */
    protected static String stripLeadingNewline(String segment) {
        if (segment.startsWith("\r\n")) {
            return segment.substring(2);
        }

        if (segment.startsWith("\n")) {
            return segment.substring(1);
        }

        return segment;
    }

    /**
     * Strip a single trailing {@code \r\n} or {@code \n} from a multipart segment.
     *
     * @param segment the segment
     * @return the segment without its trailing newline
     */
    protected static String stripTrailingNewline(String segment) {
        if (segment.endsWith("\r\n")) {
            return segment.substring(0, segment.length() - 2);
        }

        if (segment.endsWith("\n")) {
            return segment.substring(0, segment.length() - 1);
        }

        return segment;
    }
}
