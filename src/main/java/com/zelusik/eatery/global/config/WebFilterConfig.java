package com.zelusik.eatery.global.config;

import com.zelusik.eatery.global.log.filter.LogApiInfoFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

@Configuration
public class WebFilterConfig {

    /**
     * <p>
     * API 요청/응답에 대한 로그를 출력하는 filter.
     *
     * <p>
     * Spring Security에서 사용하는 filter 이전에 적용시키기 위해 Filter의 순서를 -101로 설정하였음.
     * (Spring Security filter의 기본 순서는 -100)
     */
    @Bean
    public FilterRegistrationBean<Filter> logFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LogApiInfoFilter());
        filterRegistrationBean.setOrder(-101);
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }
}
