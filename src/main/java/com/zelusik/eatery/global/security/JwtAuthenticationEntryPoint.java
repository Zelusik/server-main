package com.zelusik.eatery.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zelusik.eatery.global.exception.dto.ErrorResponse;
import com.zelusik.eatery.global.exception.constant.CustomExceptionType;
import com.zelusik.eatery.global.exception.util.ExceptionUtils;
import com.zelusik.eatery.global.log.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * 인증이 필요한 엔드포인트에 대해 인증되지 않았을 때 동작하는 handler.
     *
     * @param request       that resulted in an <code>AuthenticationException</code>
     * @param response      so that the user agent can begin authentication
     * @param authenticationException that caused the invocation
     * @throws IOException if an input or output exception occurred
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authenticationException
    ) throws IOException {
        log.warn(
                "[{}] JwtAuthenticationEntryPoint.commence() ex={}",
                LogUtils.getLogTraceId(),
                ExceptionUtils.getExceptionStackTrace(authenticationException)
        );

        response.setStatus(UNAUTHORIZED.value());
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(
                new ObjectMapper().writeValueAsString(
                        new ErrorResponse(
                                CustomExceptionType.ACCESS_DENIED.getCode(),
                                CustomExceptionType.ACCESS_DENIED.getMessage()
                        )
                )
        );
    }
}
