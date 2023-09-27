package com.zelusik.eatery.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zelusik.eatery.global.exception.dto.ErrorResponse;
import com.zelusik.eatery.global.exception.constant.ExceptionType;
import com.zelusik.eatery.global.exception.util.ExceptionUtils;
import com.zelusik.eatery.global.log.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * 엔드포인트에 대해 접근 권한이 존재하지 않을 때 동작하는 handler.
     *
     * @param request               that resulted in an <code>AccessDeniedException</code>
     * @param response              so that the user agent can be advised of the failure
     * @param accessDeniedException that caused the invocation
     * @throws IOException if an input or output exception occurred
     */
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        log.error(
                "[{}] JwtAccessDeniedHandler.handle() ex={}",
                LogUtils.getLogTraceId(),
                ExceptionUtils.getExceptionStackTrace(accessDeniedException)
        );

        response.setStatus(FORBIDDEN.value());
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(
                new ObjectMapper().writeValueAsString(
                        new ErrorResponse(
                                ExceptionType.ACCESS_DENIED.getCode(),
                                ExceptionType.ACCESS_DENIED.getMessage()
                        )
                )
        );
    }
}
