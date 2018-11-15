package com.library.base.okhttpIntercepter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;

/**
 * @author ihsan on 09/02/2017.
 */

class Printer {

    private static final int JSON_INDENT = 3;

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String DOUBLE_SEPARATOR = LINE_SEPARATOR + LINE_SEPARATOR;

    private static final String[] OMITTED_RESPONSE = {LINE_SEPARATOR, "Omitted response body"};
    private static final String[] OMITTED_REQUEST = {LINE_SEPARATOR, "Omitted request body"};

    private static final String N = "\n";
    private static final String T = "\t";
    private static final String RN = " \r\n";
    private static final String REQUEST_UP_LINE = "┌────── Request ────────────────────────────────────────────────────────────────────────";
    private static final String END_LINE = "└───────────────────────────────────────────────────────────────────────────────────────";
    private static final String RESPONSE_UP_LINE = "┌────── Response ───────────────────────────────────────────────────────────────────────";
    private static final String BODY_TAG = "Body:";
    private static final String URL_TAG = "URL: ";
    private static final String METHOD_TAG = "Method: @";
    private static final String CONTENT_TYPE_TAG = "Content-Type: ";
    private static final String HEADERS_TAG = "Headers:";
    private static final String STATUS_CODE_TAG = "Status Code: ";
    private static final String RECEIVED_TAG = "Received in: ";
    private static final String CORNER_UP = "┌ ";
    private static final String CORNER_BOTTOM = "└ ";
    private static final String CENTER_LINE = "├ ";
    private static final String DEFAULT_LINE = "│ ";

    protected Printer() {
        throw new UnsupportedOperationException();
    }

    private static boolean isEmpty(String line) {
        return TextUtils.isEmpty(line) || N.equals(line) || T.equals(line) || TextUtils.isEmpty(line.trim());
    }

    static void printJsonRequest(ConnectPrintInterceptor.Builder builder, Request request) {
        final String requestUrl = getUrlForRequest(request);
        final String[] urlLine = {URL_TAG + requestUrl, N};
        final String requestBody = LINE_SEPARATOR + BODY_TAG + LINE_SEPARATOR + dotBody(bodyToString(request));
        final String tag = builder.getTag(true);
        final StringBuilder log = new StringBuilder();
        log.append(RN).append(REQUEST_UP_LINE).append(N);

        log.append(logLines(urlLine));
        log.append(logLines(getRequest(request, builder.getLevel())));

        if (builder.getLevel() == Level.BASIC || builder.getLevel() == Level.BODY) {
            log.append(logLines(requestBody.split(LINE_SEPARATOR)));
        }

        log.append(END_LINE);
        I.log(builder.getType(), tag, log.toString());
    }

    static void printFileRequest(ConnectPrintInterceptor.Builder builder, Request request) {

        final String requestUrl = request.url().toString();
        final String[] urlLine = {URL_TAG + requestUrl, N};
        final String tag = builder.getTag(true);
        final StringBuilder log = new StringBuilder();
        log.append(RN).append(REQUEST_UP_LINE).append(N);

        log.append(logLines(urlLine));
        log.append(logLines(getRequest(request, builder.getLevel())));

        if (builder.getLevel() == Level.BASIC || builder.getLevel() == Level.BODY) {
            log.append(logLines(OMITTED_REQUEST));
        }

        log.append(END_LINE);
        I.log(builder.getType(), tag, log.toString());

//        String params = bodyToString(request);
//        Log.e("params", params);
    }

    static void printJsonResponse(ConnectPrintInterceptor.Builder builder, long chainMs, boolean isSuccessful,
                                  int code, String headers, String bodyString, List<String> segments, String message, final String responseUrl) {

        final String[] urlLine = {URL_TAG + responseUrl, N};
        final String[] response = getResponse(headers, chainMs, code, isSuccessful, builder.getLevel(), segments, message);
        final String responseBody = LINE_SEPARATOR + BODY_TAG + LINE_SEPARATOR + getJsonString(bodyString);
        final String tag = builder.getTag(false);
        final StringBuilder log = new StringBuilder();
        log.append(RN).append(RESPONSE_UP_LINE).append(N);

        log.append(logLines(urlLine));
        log.append(logLines(response));

        if (builder.getLevel() == Level.BASIC || builder.getLevel() == Level.BODY) {
            log.append(logLines(responseBody.split(LINE_SEPARATOR)));
        }

        log.append(END_LINE);
        I.log(builder.getType(), tag, log.toString());
    }

    static void printFileResponse(ConnectPrintInterceptor.Builder builder, long chainMs, boolean isSuccessful,
                                  int code, String headers, List<String> segments, String message) {
        final String tag = builder.getTag(false);
        final StringBuilder log = new StringBuilder();
        log.append(RN).append(RESPONSE_UP_LINE).append(N);

        log.append(logLines(getResponse(headers, chainMs, code, isSuccessful, builder.getLevel(), segments, message)));
        log.append(logLines(OMITTED_RESPONSE));

        log.append(END_LINE);
        I.log(builder.getType(), tag, log.toString());
    }

    static String getUrlForRequest(Request request) {
        String url = request.url().toString();
        String params = bodyToString(request);
        return url.contains("?") ? url + params : url + "?" + params;
    }

    private static String[] getRequest(Request request, Level level) {
        String log;
        String header = request.headers().toString();
        RequestBody requestBody = request.body();
        boolean loggableHeader = level == Level.HEADERS || level == Level.BASIC;
        String methodLine = METHOD_TAG + request.method() + LINE_SEPARATOR;
        String contentTypeLine = requestBody != null ? CONTENT_TYPE_TAG + requestBody.contentType().subtype() + DOUBLE_SEPARATOR : "";
        String headerLine = isEmpty(header) ? "" : loggableHeader ? HEADERS_TAG + LINE_SEPARATOR + dotHeaders(header) : "";
        log = methodLine + contentTypeLine + headerLine;
        return log.split(LINE_SEPARATOR);
    }

    private static String[] getResponse(String header, long tookMs, int code, boolean isSuccessful,
                                        Level level, List<String> segments, String message) {
        String log;
        boolean loggableHeader = level == Level.HEADERS || level == Level.BASIC;
        String segmentString = slashSegments(segments);
        log = ((!TextUtils.isEmpty(segmentString) ? segmentString + " - " : "") + "is success : "
                + isSuccessful + " - " + RECEIVED_TAG + tookMs + "ms" + DOUBLE_SEPARATOR + STATUS_CODE_TAG +
                code + " / " + message + DOUBLE_SEPARATOR + (isEmpty(header) ? "" : loggableHeader ? HEADERS_TAG + LINE_SEPARATOR +
                dotHeaders(header) : ""));
        return log.split(LINE_SEPARATOR);
    }

    private static String slashSegments(List<String> segments) {
        StringBuilder segmentString = new StringBuilder();
        for (String segment : segments) {
            segmentString.append("/").append(segment);
        }
        return segmentString.toString();
    }

    private static String dotHeaders(String header) {
        String[] headers = header.split(LINE_SEPARATOR);
        StringBuilder builder = new StringBuilder();
        String tag = "─ ";
        if (headers.length > 1) {
            for (int i = 0; i < headers.length; i++) {
                if (i == 0) {
                    tag = CORNER_UP;
                } else if (i == headers.length - 1) {
                    tag = CORNER_BOTTOM;
                } else {
                    tag = CENTER_LINE;
                }
                builder.append(tag).append(headers[i]).append(N);
            }
        } else {
            for (String item : headers) {
                builder.append(tag).append(item).append(N);
            }
        }
        return builder.toString();
    }

    private static String dotBody(String body) {
        try {
            String[] bodys = body.split("&");
            StringBuilder builder = new StringBuilder();
            String tag = "─ ";
            if (bodys.length > 1) {
                for (int i = 0; i < bodys.length; i++) {
                    if (i == 0) {
                        tag = CORNER_UP;
                    } else if (i == bodys.length - 1) {
                        tag = CORNER_BOTTOM;
                    } else {
                        tag = CENTER_LINE;
                    }
                    builder.append(tag).append(bodys[i]).append(N);
                }
            } else {
                for (String item : bodys) {
                    builder.append(tag).append(item).append(N);
                }
            }
            return builder.toString();
        } catch (Exception e) {
            return body;
        }
    }

    private static String logLines(String[] lines) {
        StringBuilder result = new StringBuilder();
        for (String line : lines) {
            if (line.equals(N)) {
                continue;
            }
            result.append(DEFAULT_LINE + line).append(N);
        }
        return result.toString();
    }

    private static String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            if (copy.body() == null)
                return "";
            copy.body().writeTo(buffer);
            return getJsonString(buffer.readUtf8());
        } catch (final IOException e) {
            return "{\"err\": \"" + e.getMessage() + "\"}";
        }
    }

    static String getJsonString(final String msg) {
        String message;
        try {
            if (msg.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(msg);
                message = jsonObject.toString(JSON_INDENT);
            } else if (msg.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(msg);
                message = jsonArray.toString(JSON_INDENT);
            } else {
                message = msg;
            }
        } catch (JSONException e) {
            message = msg;
        }
        return message;
    }
}
