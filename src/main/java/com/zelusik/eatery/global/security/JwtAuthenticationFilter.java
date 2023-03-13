package com.zelusik.eatery.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zelusik.eatery.app.dto.exception.ErrorResponse;
import com.zelusik.eatery.global.exception.ExceptionType;
import com.zelusik.eatery.global.exception.ExceptionUtils;
import com.zelusik.eatery.global.log.LogUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 모든 요청마다 작동하여, jwt token을 확인한다.
     * 유효한 token이 있는 경우 token을 parsing해서 사용자 정보를 읽고 SecurityContext에 사용자 정보를 저장한다.
     *
     * @param request     request 객체
     * @param response    response 객체
     * @param filterChain FilterChain 객체
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // Header에서 JWT 받아옴
        String token = jwtTokenProvider.getToken(request);

        if (token != null) {
            try {
                jwtTokenProvider.validateToken(token);
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception ex) {
                setErrorResponse(ex.getClass(), response);
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Exception 정보를 입력받아 응답할 error response를 설정한다.
     *
     * @param classType Exception의 class type
     * @param response  HttpServletResponse 객체
     */
    private void setErrorResponse(
            Class<? extends Exception> classType,
            HttpServletResponse response
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=UTF-8");

        ExceptionType exceptionType = ExceptionType.from(classType).orElse(ExceptionType.UNAUTHORIZED);
        ErrorResponse errorResponse = new ErrorResponse(exceptionType.getCode(), exceptionType.getMessage()
        );
        new ObjectMapper().writeValue(response.getOutputStream(), errorResponse);
    }
}
