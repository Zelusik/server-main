package com.zelusik.eatery.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zelusik.eatery.global.auth.exception.AccessTokenValidateException;
import com.zelusik.eatery.global.auth.exception.RefreshTokenValidateException;
import com.zelusik.eatery.global.auth.exception.TokenValidateException;
import com.zelusik.eatery.global.exception.constant.CustomExceptionType;
import com.zelusik.eatery.global.exception.dto.ErrorResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <code>JwtAuthenticationFilter</code>에서 발생하는 에러를 처리하기 위한 filter
 *
 * @see JwtAuthenticationFilter
 */
@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (TokenValidateException ex) {
            setErrorResponse(CustomExceptionType.TOKEN_VALIDATE, response);
        } catch (AccessTokenValidateException ex) {
            setErrorResponse(CustomExceptionType.ACCESS_TOKEN_VALIDATE, response);
        } catch (RefreshTokenValidateException ex) {
            setErrorResponse(CustomExceptionType.REFRESH_TOKEN_VALIDATE, response);
        }
    }

    /**
     * Exception 정보를 입력받아 응답할 error response를 설정한다.
     *
     * @param classType Exception의 class type
     * @param response  HttpServletResponse 객체
     */
    private void setErrorResponse(
            CustomExceptionType exceptionType,
            HttpServletResponse response
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=UTF-8");
        ErrorResponse errorResponse = new ErrorResponse(exceptionType.getCode(), exceptionType.getMessage());
        new ObjectMapper().writeValue(response.getOutputStream(), errorResponse);
    }
}
