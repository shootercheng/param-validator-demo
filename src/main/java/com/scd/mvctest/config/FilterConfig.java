package com.scd.mvctest.config;

import org.catdou.validate.filter.ParamFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.servlet.DispatcherType;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author James
 */
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<ParamFilter> createDispatchFilter() {
        FilterRegistrationBean<ParamFilter> registerBean = new FilterRegistrationBean<>();
        registerBean.setFilter(new ParamFilter());
        EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.REQUEST,
                DispatcherType.FORWARD);
        Map<String, String> initMap = new HashMap<>();
//        initMap.put("type", "json");
//        initMap.put("path", "classpath*:json/**/validate_*.json");
        initMap.put("type", "xml");
        initMap.put("path", "classpath*:xml/**/validate_*.xml");
        registerBean.setInitParameters(initMap);
        registerBean.setDispatcherTypes(dispatcherTypes);
        registerBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registerBean.addUrlPatterns("/*");
        return registerBean;
    }
}
