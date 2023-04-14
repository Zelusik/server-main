package com.zelusik.eatery.log.filter;

import com.zelusik.eatery.log.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class LogApiInfoFilter extends OncePerRequestFilter {

    private static final String[] LOG_BLACK_LIST = {
            "/swagger",
            "/v3/api-docs"
    };

    private static final List<MediaType> VISIBLE_TYPES = Arrays.asList(
            MediaType.valueOf("text/*"),
            MediaType.APPLICATION_FORM_URLENCODED,
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_XML,
            MediaType.MULTIPART_FORM_DATA
    );

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (LogUtils.getLogTraceId() == null) {
            LogUtils.setLogTraceId();
        }

        try {
            if (isAsyncDispatch(request)) {
                filterChain.doFilter(request, response);
            } else {
                boolean doLog = Arrays.stream(LOG_BLACK_LIST).noneMatch(request.getRequestURI()::contains);
                ResponseWrapper responseWrapper = new ResponseWrapper(response);
                try {
                    if (isMultipartFormData(request.getContentType())) {
                        log.info("[{}] Request: [{}] uri={}, payload=multipart/form-data", LogUtils.getLogTraceId(), request.getMethod(), request.getRequestURI());
                        filterChain.doFilter(request, responseWrapper);
                    } else {
                        RequestWrapper requestWrapper = new RequestWrapper(request);
                        if (doLog) logRequest(requestWrapper);
                        filterChain.doFilter(requestWrapper, responseWrapper);
                    }
                } finally {
                    if (doLog) logResponse(responseWrapper);
                    responseWrapper.copyBodyToResponse();
                }
            }
        } finally {
            LogUtils.removeLogTraceId();
        }
    }

    private void logRequest(RequestWrapper request) throws IOException {
        String logTraceId = LogUtils.getLogTraceId();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        if (queryString != null) {
            uri += "?" + queryString;
        }

        byte[] content = StreamUtils.copyToByteArray(request.getInputStream());
        if (ObjectUtils.isEmpty(content)) {
            log.info("[{}] Request: [{}] uri={}", logTraceId, request.getMethod(), uri);
        } else {
            String payloadInfo = getPayloadInfo(request.getContentType(), content);
            log.info("[{}] Request: [{}] uri={}, {}", logTraceId, request.getMethod(), uri, payloadInfo);
        }

    }

    private void logResponse(ContentCachingResponseWrapper response) throws IOException {
        String logTraceId = LogUtils.getLogTraceId();
        byte[] content = StreamUtils.copyToByteArray(response.getContentInputStream());
        if (ObjectUtils.isEmpty(content)) {
            log.info("[{}] Response: no content", logTraceId);
        } else {
            String payloadInfo = getPayloadInfo(response.getContentType(), content);
            log.info("[{}] Response: {}", logTraceId, payloadInfo);
        }
    }

    private String getPayloadInfo(String contentType, byte[] content) {
        String payloadInfo = "content-type=" + contentType + ", payload=";

        if (contentType == null) {
            contentType = MediaType.APPLICATION_JSON_VALUE;
        }

        if (MediaType.valueOf(contentType).equals(MediaType.valueOf("text/html")) ||
                MediaType.valueOf(contentType).equals(MediaType.valueOf("text/css"))) {
            return payloadInfo + "HTML/CSS Content";
        }
        if (!isVisible(MediaType.valueOf(contentType))) {
            return payloadInfo + "Binary Content";
        }

        if (content.length >= 10000) {
            return payloadInfo + "too many data.";
        }

        String contentString = new String(content);
        // TODO: 추가적인 content-type case에 대한 로그 출력도 고민할 필요 있음.
        if (contentType.equals(MediaType.APPLICATION_JSON_VALUE)) {
            payloadInfo += contentString.replaceAll("\n *", "").replaceAll(",", ", ");
        } else {
            payloadInfo += contentString;
        }

        return payloadInfo;
    }

    private boolean isMultipartFormData(String contentType) {
        return contentType != null && contentType.contains(MediaType.MULTIPART_FORM_DATA_VALUE);
    }

    private boolean isVisible(MediaType mediaType) {
        return VISIBLE_TYPES.stream()
                .anyMatch(visibleType -> visibleType.includes(mediaType));
    }
}
