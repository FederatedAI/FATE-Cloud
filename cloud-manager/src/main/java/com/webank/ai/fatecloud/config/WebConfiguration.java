/*
 * Copyright 2020 The FATE Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.ai.fatecloud.config;

import com.webank.ai.fatecloud.Interceptor.AutoDeployInterceptor;
import com.webank.ai.fatecloud.Interceptor.ReferrerInterceptor;
import com.webank.ai.fatecloud.Interceptor.SiteAuthorizationInterceptor;
import com.webank.ai.fatecloud.Interceptor.UserInterceptor;
import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {


    @Bean
    public AutoDeployInterceptor getAutoDeployInterceptor() {
        return new AutoDeployInterceptor();
    }


    @Bean
    public SiteAuthorizationInterceptor getSiteAuthorizationInterceptor() {
        return new SiteAuthorizationInterceptor();
    }

    @Bean
    public UserInterceptor getUserInterceptor() {
        return new UserInterceptor();
    }

    @Bean
    public ReferrerInterceptor getReferrerInterceptor() {
        return new ReferrerInterceptor();
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(getAutoDeployInterceptor())
                .addPathPatterns("/api/system/*");

        registry.addInterceptor(getSiteAuthorizationInterceptor())
                .addPathPatterns("/api/authority/*");

        registry.addInterceptor(getUserInterceptor())
                .addPathPatterns("/api/dropdown/**")

                .addPathPatterns("/api/authority/**")
                .excludePathPatterns("/api/authority/institutions", "/api/authority/institutions/approved", "/api/authority/apply", "/api/authority/applied", "/api/authority/check/partyId", "/api/authority/results")

                .addPathPatterns("/api/cloud/user/**")
                .excludePathPatterns("/api/cloud/user/login")

                .addPathPatterns("/api/fate/user/**")
                .excludePathPatterns("/api/fate/user/activate")

                .addPathPatterns("/api/function/**")
                .excludePathPatterns("/api/function/find/all/fateManager")

                .addPathPatterns("/api/group/**")

                .addPathPatterns("/api/federation/**")
                .excludePathPatterns("/api/federation/findOrganization")

                .addPathPatterns("/api/site/**")
                .excludePathPatterns("/api/site/findOneSite", "/api/site/checkAuthority", "/api/site/heart", "/api/site/page",
                        "/api/site/findOneSite/fateManager", "/api/site/checkAuthority/fateManager", "/api/site/heart/fateManager", "/api/site/page/fateManager",
                        "/api/site/checkUrl", "/api/site/activate", "/api/site/ip/accept", "/api/site/ip/query", "/api/site/fate/version", "/api/site/rollsite/checkPartyId"
                )

                .addPathPatterns("/api/system/**")
                .excludePathPatterns("/api/system/add")
                .excludePathPatterns("/api/system/heart")

                .addPathPatterns("/api/job/**")
                .excludePathPatterns("/api/job/push")

                .addPathPatterns("/api/product/**")
                .excludePathPatterns("/api/product/page/fatemanager")
                .excludePathPatterns("/api/exchange/page/fatemanager")
        ;

//        registry.addInterceptor(getReferrerInterceptor());

    }

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**").allowedMethods("*").allowedOrigins("*").allowCredentials(true);
//    }



//    @Value("${server.http.port}")
//    private int httpPort;
//
//    @Bean
//    public ServletWebServerFactory servletContainer() {
//        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
//        tomcat.addAdditionalTomcatConnectors(createStandardConnector()); // 添加http
//        return tomcat;
//    }

    // 配置http
//    private Connector createStandardConnector() {
//        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
//        connector.setPort(httpPort);
//        return connector;
//    }

}
